import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Demostracion del amaño de Config 03: deja la figura a UNA casilla de
 * completarse, que es cuando la Pantalla premia las tablas pre-fijadas.
 *
 * A diferencia de Config 02 —donde las tablas premiadas salen al COMPLETARSE el
 * Pleno— aqui la condicion es i == casillas-1, o sea justo antes de ganar.
 *
 * Uso:  java CantarFigura [pleno|u|t] [numeroDeTabla] [pausaMs]
 */
public class CantarFigura {

    /**
     * Casillas de cada figura dentro del carton, en el orden column-major que
     * usa el Conector (indice = fila + 5*columna). El centro (12) es libre.
     */
    private static int[] casillas(String figura) {
        if ("t".equals(figura)) {
            return new int[]{0, 5, 10, 15, 20, 11, 13, 14};              // 8 casillas
        }
        if ("u".equals(figura)) {
            return new int[]{0, 1, 2, 3, 4, 9, 14, 19, 20, 21, 22, 23, 24}; // 13
        }
        int[] todas = new int[25];                                        // pleno
        for (int i = 0; i < 25; i++) {
            todas[i] = i;
        }
        return todas;
    }

    private static String checkbox(String figura) {
        if ("t".equals(figura)) return "juegoLetraT";
        if ("u".equals(figura)) return "juegoLetraUGrande";
        return "juegoPleno";
    }

    private static String nombre(String figura) {
        if ("t".equals(figura)) return "Letra T";
        if ("u".equals(figura)) return "U Grande";
        return "Pleno";
    }

    /** Las 75 balotas: B=b1..b15 (1-15), I=i1..i15 (16-30), N, G, O. */
    private static String campoDe(int numero) {
        String[] col = {"b", "i", "n", "g", "o"};
        int c = (numero - 1) / 15;
        return col[c] + (numero - c * 15);
    }

    private static Object leer(Object o, String campo) throws Exception {
        Field f = o.getClass().getDeclaredField(campo);
        f.setAccessible(true);
        return f.get(o);
    }

    public static void main(String[] args) throws Exception {
        String figura = args.length > 0 ? args[0].toLowerCase() : "pleno";
        int tabla = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        long pausa = args.length > 2 ? Long.parseLong(args[2]) : 700L;

        // 1) Login: carga la partida programada de config.ker.
        Vista.Entrada entrada = new Vista.Entrada();
        Field fp = Vista.Entrada.class.getDeclaredField("passField");
        fp.setAccessible(true);
        ((JTextField) fp.get(entrada)).setText("Pantalla");
        Field fb = Vista.Entrada.class.getDeclaredField("btn_Entrar");
        fb.setAccessible(true);
        ((AbstractButton) fb.get(entrada)).doClick();

        final Object pantalla = leer(entrada, "wPantalla");
        System.out.println("figura        : " + nombre(figura));
        System.out.println("modoProgramado: " + leer(pantalla, "modoProgramado"));
        if ("pleno".equals(figura)) {
            System.out.println("pre-fijadas   : " + leer(pantalla, "codTabla10") + ", " + leer(pantalla, "codTabla11"));
        } else if ("u".equals(figura)) {
            System.out.println("pre-fijadas   : " + leer(pantalla, "codTabla20") + ", " + leer(pantalla, "codTabla21"));
        } else {
            System.out.println("pre-fijadas   : " + leer(pantalla, "codTabla30") + ", " + leer(pantalla, "codTabla31"));
        }

        // 2) Marcar el juego, o no se verifica nada.
        final JCheckBox chk = (JCheckBox) leer(pantalla, checkbox(figura));
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { chk.setSelected(true); }
        });

        // 3) Numeros de las casillas de la figura (el centro es libre).
        List<Integer> numeros = new ArrayList<>();
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:/Bingo/db/tablas.db");
             PreparedStatement st = c.prepareStatement("select * from Tablas where numTabla=?")) {
            st.setInt(1, tabla);
            try (ResultSet r = st.executeQuery()) {
                if (!r.next()) {
                    System.out.println("No existe la tabla " + tabla);
                    System.exit(1);
                }
                for (int idx : casillas(figura)) {
                    int n = r.getInt("n" + (idx + 1));
                    if (n > 0) {
                        numeros.add(n);
                    }
                }
            }
        }

        // 4) Se canta UNO MENOS: la figura queda a una casilla.
        int aCantar = numeros.size() - 1;
        System.out.println("tabla " + tabla + ": la figura tiene " + numeros.size()
                + " numeros; se cantan " + aCantar + " y se deja " + numeros.get(aCantar)
                + " sin cantar");

        final Method marcar = pantalla.getClass().getMethod("setColorNumeroSeleccionado", JLabel.class);
        for (int i = 0; i < aCantar; i++) {
            final int n = numeros.get(i);
            final JLabel balota = (JLabel) leer(pantalla, campoDe(n));
            System.out.println("  " + (i + 1) + "/" + aCantar + "  ->  " + n);
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        marcar.invoke(pantalla, balota);
                    } catch (Exception ex) {
                        System.out.println("     fallo al marcar " + n + ": " + ex.getCause());
                    }
                }
            });
            Thread.sleep(pausa);
        }

        // 5) Ganadores anunciados.
        java.util.Vector<?> v = (java.util.Vector<?>) leer(pantalla, "ganadores");
        System.out.println("ganadores anunciados: " + v.size());
        for (Object o : v) {
            System.out.println("   tabla=" + o.getClass().getMethod("getNumTabla").invoke(o)
                    + "  juego=" + o.getClass().getMethod("getJuego").invoke(o)
                    + "  codigo=" + o.getClass().getMethod("getCodigo").invoke(o));
        }
        System.out.println("(la ventana queda abierta)");
    }
}
