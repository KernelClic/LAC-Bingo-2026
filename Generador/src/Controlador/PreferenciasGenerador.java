package Controlador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Preferencias persistentes del Generador. Se guardan en el archivo BINARIO
 * {@code config.ker}, junto a la base de datos (/Bingo/db en Linux,
 * c:\Bingo\db en Windows), para que las elecciones del administrador se
 * recuerden entre ejecuciones y no queden a la vista del operador.
 *
 * <p>Hoy guarda que modos de generacion de tablas quedan disponibles: solo
 * Normal, solo Personalizada o ambas.</p>
 *
 * <p>Formato del archivo (todo en big-endian, escrito con DataOutputStream):</p>
 * <pre>
 *   int    MAGIC   = 0x4B455243  ("KERC")
 *   int    VERSION = 1
 *   int    tamaño del bloque de datos
 *   byte[] bloque de datos enmascarado (XOR con MASCARA)
 *
 *   El bloque, ya sin mascara, contiene:
 *     int  cantidad de claves
 *     por cada clave:  UTF clave, UTF valor
 * </pre>
 *
 * <p>Al ser clave/valor, se pueden agregar preferencias nuevas sin romper los
 * archivos ya escritos: las claves desconocidas se conservan al actualizar.</p>
 *
 * @author oracle
 */
public class PreferenciasGenerador {

    /* Modos de generacion que se le ofrecen al operador. */
    public static final String MODO_NORMAL = "NORMAL";
    public static final String MODO_PERSONALIZADA = "PERSONALIZADA";
    public static final String MODO_AMBAS = "AMBAS";

    /** Archivo binario de preferencias. */
    private static final String ARCHIVO = "config.ker";

    /** Archivo de texto usado antes; si aparece, se migra y se elimina. */
    private static final String ARCHIVO_ANTERIOR = "generador.properties";

    private static final int MAGIC = 0x4B455243;   // "KERC"
    private static final int VERSION = 1;

    /** Mascara para que el contenido no quede legible en un editor de texto. */
    private static final byte[] MASCARA = {
        (byte) 0x4B, (byte) 0x43, (byte) 0x7E, (byte) 0x21,
        (byte) 0x35, (byte) 0xA9, (byte) 0x5C, (byte) 0xD3
    };

    private static final String CLAVE_MODOS = "generacion.modos";

    /** Preferencias en memoria (se conserva el orden de escritura). */
    private final Map<String, String> valores = new LinkedHashMap<>();

    /**
     * Claves que ESTE objeto cambio desde la ultima lectura. Al guardar solo se
     * imponen ellas y el resto se toma del disco, para no pisar lo que hayan
     * escrito los otros programas. Importa de veras: en el mismo archivo vive
     * ahora la partida programada (claves {@code partida.*}) que escribe el
     * Config y lee la Pantalla; sobrescribir el archivo entero la borraria.
     */
    private final java.util.Set<String> modificadas = new java.util.LinkedHashSet<>();

    public PreferenciasGenerador() {
        cargar();
    }

    private static File getArchivo() {
        return new File(AccesoAleatorio.getRutaFileDB() + ARCHIVO);
    }

    private static File getArchivoAnterior() {
        return new File(AccesoAleatorio.getRutaFileDB() + ARCHIVO_ANTERIOR);
    }

    /** Ruta absoluta del archivo de preferencias, para mostrarla en pantalla. */
    public static String getRutaArchivo() {
        return getArchivo().getAbsolutePath();
    }

    // =====================================================================
    // Lectura / escritura
    // =====================================================================

    /**
     * Lee el archivo binario si existe. Si no existe, intenta migrar el archivo
     * de texto de la version anterior. Si no hay nada legible, quedan los
     * valores por defecto.
     */
    public final void cargar() {
        valores.clear();
        modificadas.clear();
        File archivo = getArchivo();
        if (archivo.exists() && archivo.isFile()) {
            leerBinario(archivo);
            return;
        }
        migrarDesdeTexto();
    }

    private void leerBinario(File archivo) {
        leerBinarioEn(archivo, valores);
    }

    private static void leerBinarioEn(File archivo, Map<String, String> destino) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(archivo))) {
            if (in.readInt() != MAGIC) {
                return;                      // archivo ajeno o dañado
            }
            int version = in.readInt();
            if (version != VERSION) {
                return;                      // version futura: no se interpreta
            }
            int largo = in.readInt();
            if (largo < 0 || largo > 1024 * 1024) {
                return;                      // tamaño no razonable
            }
            byte[] datos = new byte[largo];
            in.readFully(datos);
            desenmascarar(datos);

            DataInputStream bloque = new DataInputStream(new ByteArrayInputStream(datos));
            int n = bloque.readInt();
            for (int i = 0; i < n; i++) {
                String clave = bloque.readUTF();
                String valor = bloque.readUTF();
                destino.put(clave, valor);
            }
        } catch (IOException ex) {
            destino.clear();                 // se trabaja con los valores por defecto
        }
    }

    /**
     * Escribe las preferencias en disco. Conserva las claves que ya estuvieran
     * en el archivo, asi que sirve tanto para crear como para actualizar.
     *
     * @return true si alcanzo a guardar, false si hubo error de escritura.
     */
    public boolean guardar() {
        File archivo = getArchivo();
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        Map<String, String> aEscribir = new LinkedHashMap<>();
        if (archivo.exists() && archivo.isFile()) {
            leerBinarioEn(archivo, aEscribir);   // punto de partida: el disco
        }
        for (String clave : modificadas) {       // encima, lo propio
            aEscribir.put(clave, valores.get(clave));
        }

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (DataOutputStream bloque = new DataOutputStream(buffer)) {
                bloque.writeInt(aEscribir.size());
                for (Map.Entry<String, String> e : aEscribir.entrySet()) {
                    bloque.writeUTF(e.getKey());
                    bloque.writeUTF(e.getValue());
                }
            }
            byte[] datos = buffer.toByteArray();
            enmascarar(datos);

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(archivo))) {
                out.writeInt(MAGIC);
                out.writeInt(VERSION);
                out.writeInt(datos.length);
                out.write(datos);
            }
            valores.clear();
            valores.putAll(aEscribir);
            modificadas.clear();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Migracion de la version anterior: si quedo el archivo de texto
     * generador.properties, se toma su contenido, se guarda en binario y se
     * elimina el de texto para no dejar dos archivos de configuracion.
     */
    private void migrarDesdeTexto() {
        File anterior = getArchivoAnterior();
        if (!anterior.exists() || !anterior.isFile()) {
            return;
        }
        java.util.Properties props = new java.util.Properties();
        try (InputStream in = new FileInputStream(anterior)) {
            props.load(in);
        } catch (IOException ex) {
            return;
        }
        for (String clave : props.stringPropertyNames()) {
            valores.put(clave, props.getProperty(clave));
            modificadas.add(clave);
        }
        if (guardar()) {
            anterior.delete();
        }
    }

    // =====================================================================
    // Preferencias
    // =====================================================================

    /**
     * Modos de generacion habilitados. Por defecto AMBAS (comportamiento
     * historico del Generador).
     */
    public String getModosDisponibles() {
        String valor = valores.get(CLAVE_MODOS);
        if (valor == null) {
            return MODO_AMBAS;
        }
        valor = valor.trim().toUpperCase();
        if (MODO_NORMAL.equals(valor) || MODO_PERSONALIZADA.equals(valor)) {
            return valor;
        }
        return MODO_AMBAS;
    }

    public void setModosDisponibles(String modo) {
        valores.put(CLAVE_MODOS, modo);
        modificadas.add(CLAVE_MODOS);
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    private static void enmascarar(byte[] datos) {
        for (int i = 0; i < datos.length; i++) {
            datos[i] ^= MASCARA[i % MASCARA.length];
        }
    }

    /** XOR es simetrico: desenmascarar es aplicar la misma mascara. */
    private static void desenmascarar(byte[] datos) {
        enmascarar(datos);
    }
}
