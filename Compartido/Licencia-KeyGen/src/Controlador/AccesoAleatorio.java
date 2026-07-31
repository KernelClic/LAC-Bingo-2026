package Controlador;

/**
 * Version minima para el generador de claves: solo la ruta de la carpeta de
 * datos, que es lo unico que {@link Licencia} necesita de esta clase.
 *
 * El AccesoAleatorio completo de los aplicativos arrastra todo el paquete
 * Modelo (Tabla, Configuracion...), innecesario aqui: esta herramienta no lee
 * ni escribe la base ni el archivo de licencia, solo calcula la clave a partir
 * de un ID. Licencia.java se usa TAL CUAL la comparten los programas, para que
 * el algoritmo y el secreto no puedan divergir.
 */
public final class AccesoAleatorio {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private AccesoAleatorio() {
    }

    public static String getRutaFileDB() {
        return OS.indexOf("win") >= 0 ? "c:\\Bingo\\db\\" : "/Bingo/db/";
    }
}
