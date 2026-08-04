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
    /** "Mantenimiento": permite eliminar este mismo archivo de preferencias. */
    public static final String MODULO_MANTENIMIENTO = "MT";
    /** "Figuras": configuracion dinamica por figura, leida de matriz.txt. */
    public static final String MODULO_FIGURAS = "FG";

    private static final String ARCHIVO = "config.ker";

    private static final int MAGIC = 0x4B455243;   // "KERC"
    private static final int VERSION = 1;

    /** Mascara para que el contenido no quede legible en un editor de texto. */
    private static final byte[] MASCARA = {
        (byte) 0x4B, (byte) 0x43, (byte) 0x7E, (byte) 0x21,
        (byte) 0x35, (byte) 0xA9, (byte) 0x5C, (byte) 0xD3
    };

    private static final String CLAVE_MODULOS = "config.modulos";

    /**
     * Con que amaño se juega. Las dos formas son EXCLUYENTES y la Pantalla no
     * debe combinarlas:
     *
     * <ul>
     *   <li>{@link #MODO_EXCEPCIONES}: el amaño esta cocido en los cartones. El
     *       Generador los creo en modo Personalizada con numeros de excepcion
     *       que no se cantaran, de modo que solo las tablas ganadoras pueden
     *       completarse. La configuracion por figura NO se aplica.</li>
     *   <li>{@link #MODO_FIGURAS}: el amaño esta en este archivo (falta 1 y/o
     *       al completar). Es el valor por defecto.</li>
     * </ul>
     *
     * La escribe quien configura: el Generador al generar, y el Configurador al
     * guardar la pestaña Figuras. Manda lo ultimo que se hizo.
     */
    public static final String MODO_EXCEPCIONES = "EXCEPCIONES";
    public static final String MODO_FIGURAS = "FIGURAS";

    // OJO: la clave NO puede empezar con "partida.". AccessFile borra ese
    // prefijo entero cada vez que graba la partida programada, asi que la
    // bandera se perderia al primer guardado del configurador.
    private static final String CLAVE_MODO = "amano.modo";

    /**
     * Version del catalogo de modulos. Sirve para que un modulo NUEVO aparezca
     * en instalaciones que ya tenian una lista guardada: como la lista solo
     * enumera los habilitados, sin esto no se puede distinguir "el usuario lo
     * desactivo" de "no existia cuando guardo". Al subir la version se agregan
     * los modulos incorporados desde entonces y se respeta el resto.
     *   v1: 01, 02, 03, RG, MT
     *   v2: + FG (Figuras dinamicas)
     *   v3: - 03 (la pestaña de 16 figuras fijas la reemplaza FG). El modulo
     *          sigue existiendo y se puede volver a marcar desde la ventana
     *          oculta; solo deja de venir habilitado.
     *   v4: - 01 y 02. La 02 (premiar al completarse) la reemplazan las
     *          columnas "Completa" de FG; la 01 escribia el registro 1
     *          (intentos, mensaje, tablas) que la Pantalla carga pero NO usa
     *          al jugar. Ambas siguen disponibles desde la ventana oculta.
     */
    private static final String CLAVE_VERSION_MODULOS = "config.modulos.v";
    private static final int VERSION_MODULOS = 4;

    /** Preferencias en memoria (se conserva el orden de escritura). */
    private final Map<String, String> valores = new LinkedHashMap<>();

    /**
     * Claves que ESTE objeto cambio desde la ultima lectura. Solo ellas se
     * imponen al guardar; el resto se toma del disco, para no pisar lo que
     * hayan escrito mientras tanto el Generador Universal u otra parte de este
     * mismo programa.
     */
    private final java.util.Set<String> modificadas = new java.util.LinkedHashSet<>();

    /** Prefijos a borrar al guardar (p.ej. "partida." al regrabar la partida). */
    private final java.util.Set<String> prefijosBorrados = new java.util.LinkedHashSet<>();

    public Preferencias() {
        cargar();
    }

    private static File getArchivo() {
        return new File(AccessFile.getRutaFileDB() + ARCHIVO);
    }

    /** Ruta absoluta del archivo de preferencias, para mostrarla en pantalla. */
    public static String getRutaArchivo() {
        return getArchivo().getAbsolutePath();
    }

    /** true si el archivo de preferencias existe en disco. */
    public static boolean existeArchivo() {
        File archivo = getArchivo();
        return archivo.exists() && archivo.isFile();
    }

    /** Tamaño en bytes del archivo, o -1 si no existe. */
    public static long tamanoArchivo() {
        File archivo = getArchivo();
        return existeArchivo() ? archivo.length() : -1L;
    }

    /**
     * Borra el archivo de preferencias del disco. Ojo: el archivo es COMPARTIDO
     * con el Generador Universal, asi que tambien se lleva su clave
     * {@code generacion.modos}; ambos programas vuelven a sus valores por
     * defecto. Las preferencias que este objeto tenga en memoria no se tocan:
     * llamar despues a {@link #cargar()} para quedar en sintonia con el disco.
     *
     * @return true si el archivo quedo borrado (o si ya no estaba).
     */
    public static boolean eliminarArchivo() {
        File archivo = getArchivo();
        if (!archivo.exists()) {
            return true;
        }
        return archivo.delete();
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
        modificadas.clear();
        prefijosBorrados.clear();
        leerEn(valores);
    }

    /** Vuelca el contenido del archivo en el mapa indicado. */
    private static void leerEn(Map<String, String> destino) {
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
                destino.put(clave, valor);
            }
        } catch (IOException ex) {
            destino.clear();
        }
    }

    /**
     * Escribe las preferencias en disco. Se relee el archivo y se le aplican
     * SOLO los cambios de este objeto (claves puestas con {@link #setValor} y
     * prefijos quitados con {@link #quitarPrefijo}), de modo que lo que hayan
     * escrito mientras tanto otros programas —o el propio configurador desde
     * otra parte— sobrevive. Sirve tanto para crear como para actualizar.
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
        leerEn(aEscribir);                       // punto de partida: el disco
        for (String prefijo : prefijosBorrados) {
            java.util.Iterator<String> it = aEscribir.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().startsWith(prefijo)) {
                    it.remove();
                }
            }
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
            valores.putAll(aEscribir);           // en memoria queda lo del disco
            modificadas.clear();
            prefijosBorrados.clear();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    // =====================================================================
    // Acceso generico clave/valor
    // =====================================================================

    /** Valor de una clave, o null si no esta. */
    public String getValor(String clave) {
        return valores.get(clave);
    }

    /** Valor entero de una clave, o {@code porDefecto} si falta o no es entero. */
    public int getEntero(String clave, int porDefecto) {
        String v = valores.get(clave);
        if (v == null) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            return porDefecto;
        }
    }

    /** Fija una clave en memoria; requiere {@link #guardar()} para persistirla. */
    public void setValor(String clave, String valor) {
        valores.put(clave, valor == null ? "" : valor);
        modificadas.add(clave);
    }

    /**
     * Marca para borrado todas las claves que empiecen con el prefijo. Se
     * aplica al guardar, tanto en memoria como en disco.
     */
    public void quitarPrefijo(String prefijo) {
        prefijosBorrados.add(prefijo);
        java.util.Iterator<String> it = valores.keySet().iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (c.startsWith(prefijo)) {
                it.remove();
                modificadas.remove(c);
            }
        }
    }

    /** Modo de amaño vigente; por defecto el de configuracion por figura. */
    public String getModoPartida() {
        String v = valores.get(CLAVE_MODO);
        return MODO_EXCEPCIONES.equalsIgnoreCase(v == null ? "" : v.trim())
                ? MODO_EXCEPCIONES : MODO_FIGURAS;
    }

    /** true si el amaño esta en los cartones y no en este archivo. */
    public boolean esModoExcepciones() {
        return MODO_EXCEPCIONES.equals(getModoPartida());
    }

    public void setModoPartida(String modo) {
        setValor(CLAVE_MODO, MODO_EXCEPCIONES.equalsIgnoreCase(modo == null ? "" : modo.trim())
                ? MODO_EXCEPCIONES : MODO_FIGURAS);
    }

    /**
     * Quita del archivo la partida del esquema VIEJO (posicional, claves
     * {@code partida.*}). Se llama al guardar la configuracion por figura: a
     * partir de ahi la Pantalla ya no lee ese bloque, asi que lo unico que
     * hacen sus restos es engordar el archivo y prestarse a confusion.
     */
    public void purgarPartidaVieja() {
        quitarPrefijo(PREFIJO_PARTIDA);   // incluye partida.n, la cuenta de registros
    }

    /**
     * Deja {@code config.modulos} con la lista realmente vigente y sellada con
     * la version actual. Sin esto el archivo sigue nombrando modulos que la
     * migracion ya descarta al leer, y el valor guardado miente.
     */
    public void normalizarModulos() {
        setModulos(getModulos());
    }

    /** Cuantas claves quitaria {@link #purgarPartidaVieja()} ahora mismo. */
    public int clavesDePartidaVieja() {
        int n = 0;
        for (String c : valores.keySet()) {
            if (c.startsWith(PREFIJO_PARTIDA)) {
                n++;
            }
        }
        return n;
    }

    // =====================================================================
    // Tablas pre-fijadas POR FIGURA (partida programada, esquema dinamico)
    // =====================================================================

    /**
     * Prefijo de las claves por figura. Reemplaza al esquema viejo, que
     * guardaba las figuras por POSICION en los registros partida.2..17 y por
     * eso solo admitia 16. Aqui la clave es el NOMBRE de la figura tal como
     * aparece en matriz.txt, de modo que se configuran todas las que haya.
     *
     * <pre>
     *   figura.&lt;nombre&gt;.tabla1    carton pre-fijado 1
     *   figura.&lt;nombre&gt;.tabla2    carton pre-fijado 2
     *   figura.&lt;nombre&gt;.balotas   nro de balotas (informativo)
     *   figura.&lt;nombre&gt;.completa1..3  premiadas AL COMPLETARSE la figura
     * </pre>
     */
    private static final String PREFIJO_FIGURA = "figura.";
    /** Esquema VIEJO: la partida programada posicional. Ya no se lee si hay figuras. */
    private static final String PREFIJO_PARTIDA = "partida.";

    /** true si hay configuracion por figura (esquema nuevo) en el archivo. */
    public boolean hayFigurasConfiguradas() {
        for (String clave : valores.keySet()) {
            if (clave.startsWith(PREFIJO_FIGURA)) {
                return true;
            }
        }
        return false;
    }

    /** Tablas pre-fijadas de una figura: {tabla1, tabla2}; "-1" si no aplica. */
    public String[] getTablasFigura(String figura) {
        if (figura == null) {
            return new String[]{"-1", "-1"};
        }
        String base = PREFIJO_FIGURA + figura.trim() + ".";
        String t1 = valores.get(base + "tabla1");
        String t2 = valores.get(base + "tabla2");
        return new String[]{t1 == null || t1.isEmpty() ? "-1" : t1,
                            t2 == null || t2.isEmpty() ? "-1" : t2};
    }

    /**
     * Tablas premiadas al COMPLETARSE la figura (hasta 3). Es la modalidad que
     * antes vivia en el registro 2 (pestaña 02) y solo alcanzaba al checkbox
     * "Pleno" del esquema viejo; aqui aplica a cualquier figura del catalogo.
     */
    public String[] getCompletaFigura(String figura) {
        if (figura == null) {
            return new String[]{"-1", "-1", "-1"};
        }
        String base = PREFIJO_FIGURA + figura.trim() + ".";
        String[] r = new String[3];
        for (int i = 0; i < 3; i++) {
            String v = valores.get(base + "completa" + (i + 1));
            r[i] = (v == null || v.isEmpty()) ? "-1" : v;
        }
        return r;
    }

    /** Fija las tablas premiadas al completarse; requiere {@link #guardar()}. */
    public void setCompletaFigura(String figura, String c1, String c2, String c3) {
        if (figura == null || figura.trim().isEmpty()) {
            return;
        }
        String base = PREFIJO_FIGURA + figura.trim() + ".";
        String[] v = {c1, c2, c3};
        for (int i = 0; i < 3; i++) {
            setValor(base + "completa" + (i + 1),
                    v[i] == null || v[i].trim().isEmpty() ? "-1" : v[i].trim());
        }
    }

    public int getBalotasFigura(String figura) {
        return figura == null ? 0 : getEntero(PREFIJO_FIGURA + figura.trim() + ".balotas", 0);
    }

    /** Fija la configuracion de una figura; requiere {@link #guardar()}. */
    public void setFigura(String figura, String tabla1, String tabla2, int balotas) {
        if (figura == null || figura.trim().isEmpty()) {
            return;
        }
        String base = PREFIJO_FIGURA + figura.trim() + ".";
        setValor(base + "tabla1", tabla1 == null || tabla1.trim().isEmpty() ? "-1" : tabla1.trim());
        setValor(base + "tabla2", tabla2 == null || tabla2.trim().isEmpty() ? "-1" : tabla2.trim());
        setValor(base + "balotas", Integer.toString(balotas));
    }

    /** Borra toda la configuracion por figura (antes de regrabarla). */
    public void limpiarFiguras() {
        quitarPrefijo(PREFIJO_FIGURA);
    }

    // =====================================================================
    // Modulos habilitados
    // =====================================================================

    /**
     * Modulos del configurador habilitados, en orden 01, 02, 03, MT, RG. Por
     * defecto todos. Nunca devuelve la lista vacia: si el archivo trae basura o
     * quedo sin modulos, se asumen todos.
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
            lista.add(MODULO_RANGOS);
            lista.add(MODULO_MANTENIMIENTO);
            lista.add(MODULO_FIGURAS);
        } else if (getEntero(CLAVE_VERSION_MODULOS, 1) < VERSION_MODULOS) {
            // Lista guardada por una version anterior: se habilita lo agregado
            // desde entonces (para que no quede invisible) y se retira lo que
            // quedo obsoleto.
            if (!lista.contains(MODULO_FIGURAS)) {
                lista.add(MODULO_FIGURAS);
            }
            lista.remove(MODULO_01);
            lista.remove(MODULO_02);
            lista.remove(MODULO_03);
        }
        // Solo para dejar el archivo prolijo; el orden de las pestañas lo fija
        // Vista/Config al armar la tira.
        java.util.Collections.sort(lista);
        return lista;
    }

    private static boolean esModulo(String m) {
        return MODULO_01.equals(m) || MODULO_02.equals(m) || MODULO_03.equals(m)
                || MODULO_RANGOS.equals(m) || MODULO_MANTENIMIENTO.equals(m)
                || MODULO_FIGURAS.equals(m);
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
        setValor(CLAVE_MODULOS, sb.toString());
        setValor(CLAVE_VERSION_MODULOS, Integer.toString(VERSION_MODULOS));
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
