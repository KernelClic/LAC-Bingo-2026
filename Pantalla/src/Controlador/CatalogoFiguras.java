package Controlador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Lee el catalogo de figuras de {@code /Bingo/db/matriz.txt}.
 *
 * <p>Es la fuente de verdad de QUE figuras existen. Antes el configurador tenia
 * 16 filas dibujadas a mano y {@code config.ker} las guardaba por POSICION
 * (registro 3 = figura 1, registro 4 = figura 2...), de modo que las figuras
 * de matriz.txt sin fila equivalente no se podian configurar. Leyendo el
 * catalogo, el configurador se arma solo con las figuras que realmente hay.</p>
 *
 * <p>Formato de matriz.txt: una cabecera {@code # NombreInterno | NombreMostrar}
 * seguida de 5 lineas de 5 caracteres (X = casilla de la figura).</p>
 */
public final class CatalogoFiguras {

    /** Una figura del catalogo. */
    public static final class Figura {

        private final String nombre;     // interno, el que usa la configuracion
        private final String mostrar;    // traducido, el que ve el operador
        private final List<Integer> casillas;

        Figura(String nombre, String mostrar, List<Integer> casillas) {
            this.nombre = nombre;
            this.mostrar = mostrar;
            this.casillas = casillas;
        }

        public String getNombre() {
            return nombre;
        }

        public String getMostrar() {
            return mostrar;
        }

        /** Indices 0..24 que componen la figura (fila + 5*columna). */
        public List<Integer> getCasillas() {
            return casillas;
        }

        @Override
        public String toString() {
            return nombre.equals(mostrar) ? nombre : nombre + " (" + mostrar + ")";
        }
    }

    private CatalogoFiguras() {
    }

    public static File getArchivo() {
        return new File(AccessFile.getRutaFileDB() + "matriz.txt");
    }

    /**
     * Figuras definidas en matriz.txt, en el orden del archivo. Lista vacia si
     * el archivo no existe o no es legible: el programa sigue funcionando, solo
     * que sin figuras configurables.
     */
    public static List<Figura> leer() {
        List<Figura> figuras = new ArrayList<>();
        File archivo = getArchivo();
        if (!archivo.exists() || !archivo.isFile()) {
            return figuras;
        }
        try (Scanner sc = new Scanner(archivo, "UTF-8")) {
            String nombre = null;
            String mostrar = null;
            List<String> filas = new ArrayList<>();
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                if (linea.startsWith("#")) {
                    agregar(figuras, nombre, mostrar, filas);
                    String cab = linea.substring(1).trim();
                    int p = cab.indexOf('|');
                    nombre = p >= 0 ? cab.substring(0, p).trim() : cab;
                    mostrar = p >= 0 ? cab.substring(p + 1).trim() : nombre;
                    filas = new ArrayList<>();
                } else if (!linea.trim().isEmpty()) {
                    filas.add(linea);
                }
            }
            agregar(figuras, nombre, mostrar, filas);
        } catch (IOException ex) {
            // catalogo ilegible: se devuelve lo que se alcanzo a leer
        }
        return figuras;
    }

    private static void agregar(List<Figura> figuras, String nombre, String mostrar, List<String> filas) {
        if (nombre == null || nombre.isEmpty() || filas.size() < 5) {
            return;
        }
        List<Integer> casillas = new ArrayList<>();
        for (int fila = 0; fila < 5; fila++) {
            String f = filas.get(fila);
            for (int col = 0; col < 5 && col < f.length(); col++) {
                char c = f.charAt(col);
                if (c == 'X' || c == 'x') {
                    casillas.add(fila + 5 * col);      // mismo orden que el Conector
                }
            }
        }
        if (!casillas.isEmpty()) {
            figuras.add(new Figura(nombre, mostrar, casillas));
        }
    }
}
