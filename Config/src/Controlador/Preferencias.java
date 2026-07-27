package Controlador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Preferencias persistentes del Config Universal. Se guardan en el archivo
 * BINARIO {@code config.ker}, junto a la base de datos (/Bingo/db en Linux,
 * c:\Bingo\db en Windows).
 *
 * <p>Es el MISMO archivo que usa el Generador Universal (que guarda ahi su
 * clave {@code generacion.modos}). El formato es clave/valor y al guardar se
 * conservan las claves que no conoce este programa, asi que ambos aplicativos
 * comparten el archivo sin pisarse. <b>Si se cambia el formato hay que
 * cambiarlo en los dos.</b> Candidato a subir a {@code Compartido/}.</p>
 *
 * <p>Formato (big-endian, DataOutputStream):</p>
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
 * <p>Clave propia de este programa: {@code config.modulos}, con la lista de
 * modulos habilitados separados por coma (p.ej. {@code 01,03,RG}).</p>
 *
 * @author oracle
 */
public class Preferencias {

    /* Modulos opcionales del configurador. */
    public static final String MODULO_01 = "01";
    public static final String MODULO_02 = "02";
    public static final String MODULO_03 = "03";
    /** "Rangos de Tablas": la pestaña comun, siempre la ultima de la tira. */
    public static final String MODULO_RANGOS = "RG";

    private static final String ARCHIVO = "config.ker";

    private static final int MAGIC = 0x4B455243;   // "KERC"
    private static final int VERSION = 1;

    /** Mascara para que el contenido no quede legible en un editor de texto. */
    private static final byte[] MASCARA = {
        (byte) 0x4B, (byte) 0x43, (byte) 0x7E, (byte) 0x21,
        (byte) 0x35, (byte) 0xA9, (byte) 0x5C, (byte) 0xD3
    };

    private static final String CLAVE_MODULOS = "config.modulos";

    /** Preferencias en memoria (se conserva el orden de escritura). */
    private final Map<String, String> valores = new LinkedHashMap<>();

    public Preferencias() {
        cargar();
    }

    private static File getArchivo() {
        return new File(AccesoAleatorio.getRutaFileDB() + ARCHIVO);
    }

    /** Ruta absoluta del archivo de preferencias, para mostrarla en pantalla. */
    public static String getRutaArchivo() {
        return getArchivo().getAbsolutePath();
    }

    // =====================================================================
    // Lectura / escritura
    // =====================================================================

    /**
     * Lee el archivo binario si existe. Si no existe o no es legible, quedan
     * los valores por defecto (todos los modulos habilitados).
     */
    public final void cargar() {
        valores.clear();
        File archivo = getArchivo();
        if (!archivo.exists() || !archivo.isFile()) {
            return;
        }
        try (DataInputStream in = new DataInputStream(new FileInputStream(archivo))) {
            if (in.readInt() != MAGIC) {
                return;                      // archivo ajeno o dañado
            }
            if (in.readInt() != VERSION) {
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
                valores.put(clave, valor);
            }
        } catch (IOException ex) {
            valores.clear();
        }
    }

    /**
     * Escribe las preferencias en disco. Conserva las claves de los otros
     * programas, asi que sirve tanto para crear como para actualizar.
     *
     * @return true si alcanzo a guardar, false si hubo error de escritura.
     */
    public boolean guardar() {
        File archivo = getArchivo();
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (DataOutputStream bloque = new DataOutputStream(buffer)) {
                bloque.writeInt(valores.size());
                for (Map.Entry<String, String> e : valores.entrySet()) {
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
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    // =====================================================================
    // Modulos habilitados
    // =====================================================================

    /**
     * Modulos del configurador habilitados, en orden 01, 02, 03, RG. Por
     * defecto los cuatro. Nunca devuelve la lista vacia: si el archivo trae
     * basura o quedo sin modulos, se asumen todos.
     */
    public List<String> getModulos() {
        List<String> lista = new ArrayList<>();
        String valor = valores.get(CLAVE_MODULOS);
        if (valor != null) {
            for (String parte : valor.split(",")) {
                String m = parte.trim();
                if (esModulo(m) && !lista.contains(m)) {
                    lista.add(m);
                }
            }
        }
        if (lista.isEmpty()) {
            lista.add(MODULO_01);
            lista.add(MODULO_02);
            lista.add(MODULO_03);
            lista.add(MODULO_RANGOS);
        }
        // "RG" ordena despues de los digitos, asi que Rangos queda de ultima.
        java.util.Collections.sort(lista);
        return lista;
    }

    private static boolean esModulo(String m) {
        return MODULO_01.equals(m) || MODULO_02.equals(m)
                || MODULO_03.equals(m) || MODULO_RANGOS.equals(m);
    }

    public boolean tieneModulo(String modulo) {
        return getModulos().contains(modulo);
    }

    /** Guarda en memoria la combinacion elegida; requiere {@link #guardar()}. */
    public void setModulos(List<String> modulos) {
        StringBuilder sb = new StringBuilder();
        for (String m : modulos) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(m);
        }
        valores.put(CLAVE_MODULOS, sb.toString());
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
