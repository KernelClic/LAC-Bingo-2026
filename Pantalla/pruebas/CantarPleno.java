import java.lang.reflect.Field;
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
 * Lanzador de demostracion: abre la Pantalla, activa el juego Pleno y va
 * marcando SOLO los numeros de una tabla, con una pausa entre cada uno, hasta
 * completarla. Sirve para ver en vivo el modo "partida programada": al cantarse
 * el ultimo numero deben anunciarse tambien las tablas premiadas que se
 * configuraron en el registro 2 (Config 02).
 *
 * Uso:  java CantarPleno [numeroDeTabla] [pausaMs]
 *       por defecto: tabla 1, pausa 700 ms
 */
public class CantarPleno {

    /** Las 75 balotas: B=b1..b15 (1-15), I=i1..i15 (16-30), N, G, O. */
    private static String campoDe(int numero) {
        String[] col = {"b", "i", "n", "g", "o"};
        int c = (numero - 1) / 15;          // 0..4
        int idx = numero - c * 15;          // 1..15
        return col[c] + idx;
    }

    private static Object leer(Object o, String campo) throws Exception {
        Field f = o.getClass().getDeclaredField(campo);
        f.setAccessible(true);
        return f.get(o);
    }

    public static void main(String[] args) throws Exception {
        int tabla = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        final long pausa = args.length > 1 ? Long.parseLong(args[1]) : 700L;

        // 1) Login: es quien lee config.ker y activa el modo programado.
        Vista.Entrada entrada = new Vista.Entrada();
        Field fp = Vista.Entrada.class.getDeclaredField("passField");
        fp.setAccessible(true);
        ((JTextField) fp.get(entrada)).setText("Pantalla");
        Field fb = Vista.Entrada.class.getDeclaredField("btn_Entrar");
        fb.setAccessible(true);
        ((AbstractButton) fb.get(entrada)).doClick();

        final Object pantalla = leer(entrada, "wPantalla");
        System.out.println("modoProgramado = " + leer(pantalla, "modoProgramado"));
        System.out.println("tablas premiadas = " + leer(pantalla, "pT01") + ", "
                + leer(pantalla, "pT02") + ", " + leer(pantalla, "pT03"));

        // 2) El juego Pleno debe estar marcado o no se verifica nada.
        final JCheckBox pleno = (JCheckBox) leer(pantalla, "juegoPleno");
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { pleno.setSelected(true); }
        });

        // 3) Numeros de la tabla objetivo (se salta el -1 del centro libre).
        List<Integer> numeros = new ArrayList<>();
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:/Bingo/db/tablas.db");
             PreparedStatement st = c.prepareStatement("select * from Tablas where numTabla=?")) {
            st.setInt(1, tabla);
            try (ResultSet r = st.executeQuery()) {
                if (!r.next()) {
                    System.out.println("No existe la tabla " + tabla);
                    System.exit(1);
                }
                for (int i = 1; i <= 25; i++) {
                    int n = r.getInt("n" + i);
                    if (n > 0) {
                        numeros.add(n);
                    }
                }
            }
        }
        System.out.println("cantando " + numeros.size() + " numeros de la tabla " + tabla
                + " (pausa " + pausa + " ms)");

        // 4) Marcar uno a uno, igual que un doble clic del operador.
        final java.lang.reflect.Method marcar = pantalla.getClass()
                .getMethod("setColorNumeroSeleccionado", JLabel.class);
        for (int i = 0; i < numeros.size(); i++) {
            final int n = numeros.get(i);
            final JLabel balota = (JLabel) leer(pantalla, campoDe(n));
            System.out.println("  " + (i + 1) + "/" + numeros.size() + "  ->  " + n);
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
        System.out.println("listo: tabla " + tabla + " completa.");

        // 5) Quien quedo anunciado como ganador.
        Object gan = leer(pantalla, "ganadores");
        java.util.Vector<?> v = (java.util.Vector<?>) gan;
        System.out.println("ganadores anunciados: " + v.size());
        for (Object o : v) {
            Object num = o.getClass().getMethod("getNumTabla").invoke(o);
            Object jue = o.getClass().getMethod("getJuego").invoke(o);
            Object cod = o.getClass().getMethod("getCodigo").invoke(o);
            System.out.println("   tabla=" + num + "  juego=" + jue + "  codigo=" + cod);
        }
        System.out.println("(la ventana queda abierta)");
    }
}
