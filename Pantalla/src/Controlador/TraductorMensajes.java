/*
 * Globalización de los mensajes de figuras ganadoras del Bingo.
 */
package Controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce (globaliza) los nombres de las figuras ganadoras a partir de un
 * archivo de configuración editable por el operador.
 *
 * El archivo asigna el nombre interno que usa el programa al texto que se
 * mostrará en el evento ganador. Formato (UTF-8), una asignación por línea:
 *
 *     NombreInterno = TextoAMostrar
 *
 * - Las líneas vacías y las que empiezan con '#' se ignoran.
 * - Solo debe editarse el lado derecho del '='. Se permiten acentos y espacios.
 * - Si el archivo no existe, se crea automáticamente con todos los nombres por
 *   defecto (traducción identidad) para que el operador lo edite.
 * - Si falta una clave o su valor está vacío, se muestra el nombre interno tal
 *   cual (nunca se pierde el mensaje).
 *
 * @author Administrador del Sistema
 */
public final class TraductorMensajes {

    /** Nombres internos de las figuras, tal como los crea {@code Conector}. */
    private static final String[] FIGURAS = {
        "Pleno",
        "Cruz Pequeña",
        "Cruz Grande",
        "Cuatro Esquinas",
        "Machetazo Izquierdo",
        "Machetazo Derecho",
        "Punta de Flecha",
        "Vertical Central",
        "Horizontal Central",
        "Letra T",
        "Letra L",
        "Letra X",
        "Letra U Pequeña",
        "Letra U Grande"
    };

    private static final String NOMBRE_ARCHIVO = "mensajes_figuras.cfg";

    private static Map<String, String> mapa;

    private TraductorMensajes() {
    }

    /**
     * Ruta del archivo de configuración. Reutiliza la carpeta de datos del
     * programa ({@code /Bingo/db/} en Linux/Mac, {@code c:\Bingo\db\} en
     * Windows) para mantener la coherencia con el resto de archivos de config.
     */
    public static String getRutaArchivo() {
        return AccesoAleatorio.getRutaFileDB() + NOMBRE_ARCHIVO;
    }

    /**
     * Carga (o recarga) el mapa de traducciones desde el archivo. Si el archivo
     * no existe, primero genera la plantilla con los valores por defecto.
     */
    public static synchronized void cargar() {
        mapa = new LinkedHashMap<String, String>();
        File archivo = new File(getRutaArchivo());
        if (!archivo.exists()) {
            crearPlantilla(archivo);
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(archivo), StandardCharsets.UTF_8));
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                int eq = linea.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String clave = linea.substring(0, eq).trim();
                String valor = linea.substring(eq + 1).trim();
                if (!clave.isEmpty()) {
                    mapa.put(clave, valor.isEmpty() ? clave : valor);
                }
            }
        } catch (Exception ex) {
            System.err.println("TraductorMensajes: no se pudo leer "
                    + getRutaArchivo() + " (" + ex.getMessage()
                    + "). Se usarán los nombres internos.");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    /** Genera el archivo de plantilla con la traducción identidad por defecto. */
    private static void crearPlantilla(File archivo) {
        Writer w = null;
        try {
            File dir = archivo.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            w = new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8);
            w.write("# ==============================================================\r\n");
            w.write("# Configuracion de mensajes de figuras ganadoras (Bingo)\r\n");
            w.write("# --------------------------------------------------------------\r\n");
            w.write("# Formato:   NombreInterno = TextoAMostrar\r\n");
            w.write("# Edite SOLO el lado derecho del '='. Se permiten acentos y espacios.\r\n");
            w.write("# Ejemplos:  Pleno = Apagon\r\n");
            w.write("#            Cuatro Esquinas = Pinos\r\n");
            w.write("#            Machetazo Izquierdo = Raya Izquierda\r\n");
            w.write("# Las lineas vacias o que empiezan con '#' se ignoran.\r\n");
            w.write("# ==============================================================\r\n");
            w.write("\r\n");
            for (String figura : FIGURAS) {
                w.write(figura + " = " + figura + "\r\n");
            }
        } catch (Exception ex) {
            System.err.println("TraductorMensajes: no se pudo crear la plantilla "
                    + getRutaArchivo() + " (" + ex.getMessage() + ").");
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    /**
     * Devuelve el texto a mostrar para una figura. Si no hay traducción
     * configurada, devuelve el nombre interno sin cambios.
     */
    public static String traducir(String nombreInterno) {
        if (nombreInterno == null) {
            return "";
        }
        if (mapa == null) {
            cargar();
        }
        String t = mapa.get(nombreInterno.trim());
        return (t == null || t.isEmpty()) ? nombreInterno : t;
    }
}
