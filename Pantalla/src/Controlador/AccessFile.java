package Controlador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.*;

/**
 * Acceso a la configuracion de partida programada.
 *
 * <p><b>Archivo unico.</b> Desde la unificacion, los registros de
 * {@link Modelo.Configuracion} NO viven mas en el binario de registro fijo
 * {@code /Bingo/db/config.dat}, sino como claves dentro de
 * {@code /Bingo/db/config.ker} (ver {@link Preferencias}), el mismo archivo que
 * guarda las preferencias del configurador y del Generador Universal. Un solo
 * archivo para todo.</p>
 *
 * <p>Claves usadas:</p>
 * <pre>
 *   partida.n            cantidad de registros
 *   partida.&lt;i&gt;.id       \
 *   partida.&lt;i&gt;.intento   |
 *   partida.&lt;i&gt;.juego     |  un bloque por registro, i = 0..n-1
 *   partida.&lt;i&gt;.tabN      |
 *   partida.&lt;i&gt;.tablaN   /
 * </pre>
 *
 * <p>La API publica se conservo igual que cuando escribia config.dat, para no
 * tocar a los llamadores: los registros se mantienen en memoria y se vuelcan a
 * disco en cada cambio, asi que no hace falta acordarse de {@link #cerrar()}.
 * Si al arrancar aparece un {@code config.dat} del formato viejo, se importa
 * una sola vez y se borra.</p>
 */
public class AccessFile {

    /** Prefijo de todas las claves de la partida dentro de config.ker. */
    private static final String PREFIJO = "partida.";
    private static final String CLAVE_CANTIDAD = PREFIJO + "n";

    /** Nombre del binario viejo, que solo se lee para migrar. */
    private static final String ARCHIVO_HEREDADO = "config.dat";

    /** Tamaño de registro del formato viejo (solo para leerlo al migrar). */
    private static final int tamañoRegistro = 1000;

    /** Registros en memoria; es la copia viva de lo que hay en config.ker. */
    private static final List<Configuracion> registros = new ArrayList<>();

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final String RutaWinDB = "c:\\Bingo\\db\\";
    private static final String RutaLinDB = "/Bingo/db/";

    private static final String FileRutaWindb = "c:\\windows\\system\\windl1.dll";
    private static final String FileRutaLindb = "/usr/readMe.txt";

    private static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    private static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static String getRutaFileDB() {
        if (isWindows()) {
            return RutaWinDB;
        } else if (isMac()) {
            return RutaLinDB;
        } else if (isUnix()) {
            return RutaLinDB;
        }
        return null;
    }

    public static String getRutaFiledb() {
        if (isWindows()) {
            return FileRutaWindb;
        } else if (isMac()) {
            return FileRutaLindb;
        } else if (isUnix()) {
            return FileRutaLindb;
        }
        return null;
    }

    /** Existencia de un archivo cualquiera (tablas.db, marca de licencia...). */
    public static boolean buscarFile(File archivo) throws IOException {
        if (!archivo.exists() || !archivo.isFile()) {
            return false;
        }
        return true;
    }

    // =====================================================================
    // Partida programada
    // =====================================================================

    /** true si hay una partida programada guardada (mira el disco). */
    public static boolean hayPartida() {
        migrarSiHaceFalta();
        return new Preferencias().getEntero(CLAVE_CANTIDAD, 0) > 0;
    }

    /** Empieza una partida vacia; los registros se agregan con añadirConf. */
    public static void nuevaPartida() {
        registros.clear();
        volcar();
    }

    /** Trae a memoria la partida guardada en config.ker. */
    public static void cargarPartida() {
        migrarSiHaceFalta();
        registros.clear();
        Preferencias prefs = new Preferencias();
        int n = prefs.getEntero(CLAVE_CANTIDAD, 0);
        for (int i = 0; i < n; i++) {
            registros.add(leerRegistro(prefs, i));
        }
    }

    /**
     * @deprecated Quedo por compatibilidad: el argumento se ignora, porque la
     * partida ya no vive en un archivo propio. Usar {@link #nuevaPartida()}.
     */
    @Deprecated
    public static void crearFileTablas(File archivo) throws IOException {
        nuevaPartida();
    }

    /**
     * @deprecated Quedo por compatibilidad: el argumento se ignora. Usar
     * {@link #cargarPartida()}.
     */
    @Deprecated
    public static void leerFileTablas(File archivo) throws IOException {
        cargarPartida();
    }

    /** Ya no hay flujo abierto; se conserva porque los llamadores la invocan. */
    public static void cerrar() throws IOException {
        volcar();
    }

    public static int getNumeroRegistros() {
        return registros.size();
    }

    public static boolean setConf(int i, Configuracion tabla) throws IOException {
        if (i < 0 || i > registros.size()) {
            System.out.println("\nNúmero de registro fuera de límites.");
            return false;
        }
        if (i == registros.size()) {
            registros.add(tabla);
        } else {
            registros.set(i, tabla);
        }
        volcar();
        return true;
    }

    public static void añadirConf(Configuracion tabla) throws IOException {
        setConf(registros.size(), tabla);
    }

    public static Configuracion getConf(int i) throws IOException {
        if (i < 0 || i >= registros.size()) {
            System.out.println("\nNúmero de registro fuera de límites.");
            return null;
        }
        return registros.get(i);
    }

    public static int buscarRegistro(int buscado) throws IOException {
        if (buscado == 0) {
            return -1;
        }
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getId() == buscado) {
                return i;
            }
        }
        return -1;
    }

    // =====================================================================
    // Persistencia en config.ker
    // =====================================================================

    /** Escribe los registros en memoria dentro de config.ker. */
    private static void volcar() {
        Preferencias prefs = new Preferencias();
        prefs.quitarPrefijo(PREFIJO);            // fuera los registros viejos
        prefs.setValor(CLAVE_CANTIDAD, Integer.toString(registros.size()));
        for (int i = 0; i < registros.size(); i++) {
            Configuracion c = registros.get(i);
            String p = PREFIJO + i + ".";
            prefs.setValor(p + "id", Integer.toString(c.getId()));
            prefs.setValor(p + "intento", Integer.toString(c.getIntento()));
            prefs.setValor(p + "juego", c.getJuego());
            prefs.setValor(p + "tab1", Integer.toString(c.getTab1()));
            prefs.setValor(p + "tabla1", c.getTabla1());
            prefs.setValor(p + "tab2", Integer.toString(c.getTab2()));
            prefs.setValor(p + "tabla2", c.getTabla2());
            prefs.setValor(p + "tab3", Integer.toString(c.getTab3()));
            prefs.setValor(p + "tabla3", c.getTabla3());
        }
        prefs.guardar();
    }

    private static Configuracion leerRegistro(Preferencias prefs, int i) {
        String p = PREFIJO + i + ".";
        return new Configuracion(
                prefs.getEntero(p + "id", 0),
                prefs.getEntero(p + "intento", 0),
                texto(prefs.getValor(p + "juego")),
                prefs.getEntero(p + "tab1", 0), texto(prefs.getValor(p + "tabla1")),
                prefs.getEntero(p + "tab2", 0), texto(prefs.getValor(p + "tabla2")),
                prefs.getEntero(p + "tab3", 0), texto(prefs.getValor(p + "tabla3")));
    }

    private static String texto(String v) {
        return v == null ? "N/A" : v;
    }

    // =====================================================================
    // Migracion del formato viejo (config.dat)
    // =====================================================================

    /**
     * Si quedo un config.dat del formato anterior, se pasa su contenido a
     * config.ker y se lo borra. Corre una sola vez: despues el archivo ya no
     * existe. Si config.ker ya tuviera partida, el viejo se descarta sin leer,
     * porque el vigente es el unificado.
     */
    private static void migrarSiHaceFalta() {
        File viejo = new File(getRutaFileDB() + ARCHIVO_HEREDADO);
        if (!viejo.exists() || !viejo.isFile()) {
            return;
        }
        if (new Preferencias().getEntero(CLAVE_CANTIDAD, 0) > 0) {
            viejo.delete();                      // ya migrado en una corrida previa
            return;
        }
        List<Configuracion> leidos = new ArrayList<>();
        try (RandomAccessFile flujo = new RandomAccessFile(viejo, "r")) {
            int n = (int) Math.ceil((double) flujo.length() / (double) tamañoRegistro);
            for (int i = 0; i < n; i++) {
                flujo.seek((long) i * tamañoRegistro);
                leidos.add(new Configuracion(flujo.readInt(), flujo.readInt(),
                        flujo.readUTF(),
                        flujo.readInt(), flujo.readUTF(),
                        flujo.readInt(), flujo.readUTF(),
                        flujo.readInt(), flujo.readUTF()));
            }
        } catch (IOException ex) {
            // Archivo viejo ilegible o truncado: se conserva lo que se alcanzo
            // a leer y se sigue; no vale la pena abortar el arranque por esto.
        }
        if (!leidos.isEmpty()) {
            registros.clear();
            registros.addAll(leidos);
            volcar();
        }
        viejo.delete();
    }
}
