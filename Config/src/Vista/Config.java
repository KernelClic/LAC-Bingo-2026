/*
 * Config Universal — unifica en una sola ventana las tres variantes del
 * configurador (01, 02, 03) mediante pestañas (JTabbedPane). Cada pestaña
 * reutiliza integramente la UI y la logica de la variante correspondiente
 * (se reparenta su contentPane). Se quito el cambio de contraseña; la
 * validacion por licencia se aplica en Entrada (node-lock, Controlador.Licencia).
 *
 * "Rangos de Tablas" es comun a las tres variantes, asi que se saca de las
 * pestañas internas y se muestra UNA sola vez como pestaña principal.
 *
 * Los modulos visibles son configurables: con Ctrl+Shift+DobleClic sobre la
 * tira de pestañas se abre Vista/OpcionesConfig, donde el administrador
 * habilita cualquier combinacion de 01, 02, 03 y la propia "Rangos de Tablas".
 * La eleccion se guarda en el archivo binario /Bingo/db/config.ker (ver
 * Controlador/Preferencias).
 */
package Vista;

import Controlador.Preferencias;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JTabbedPane;

public class Config extends javax.swing.JFrame {

    /** Titulo de la pestaña interna que se promueve a pestaña principal. */
    private static final String TAB_RANGOS = "Rangos de Tablas";

    // Se conservan las referencias vivas para que sus listeners sigan operando
    // aunque su contentPane se muestre dentro de una pestaña de esta ventana.
    private Config01 c01;
    private Config02 c02;
    private Config03 c03;
    private javax.swing.JTabbedPane tabs;

    /** Panel de rangos promovido (el de Config01; los otros dos se descartan). */
    private Component rangos;

    private Preferencias prefs;

    public Config() throws IOException, SQLException {
        c01 = new Config01();
        c02 = new Config02();
        c03 = new Config03();

        // "Rangos de Tablas" es identico en las tres variantes: se conserva el
        // de Config01 para la pestaña principal y se quita de las tres internas
        // para no repetirlo. Los listeners que lo operan son los de Config01,
        // que sigue vivo aunque su modulo este deshabilitado.
        rangos = quitarTab(c01, TAB_RANGOS);
        quitarTab(c02, TAB_RANGOS);
        quitarTab(c03, TAB_RANGOS);

        prefs = new Preferencias();

        tabs = new javax.swing.JTabbedPane();
        aplicarModulos();

        // Gesto oculto: Ctrl+Shift+DobleClic sobre la tira de pestañas abre la
        // ventana de modulos. (Dentro del contenido de una pestaña el clic lo
        // atiende el control que este debajo, por eso el gesto va aqui arriba.)
        tabs.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.isControlDown() && e.isShiftDown()) {
                    abrirOpcionesModulos();
                }
            }
        });

        setTitle("Configuración Universal — KernelClic");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setContentPane(tabs);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Abre la ventana oculta de modulos y, si el administrador guardo, rearma
     * las pestañas de inmediato.
     */
    private void abrirOpcionesModulos() {
        OpcionesConfig dlg = new OpcionesConfig(this, prefs);
        dlg.setVisible(true);
        if (dlg.isAceptado()) {
            aplicarModulos();
        }
    }

    /**
     * Deja como pestañas solo los modulos habilitados en las preferencias,
     * conservando el orden 01, 02, 03 y, de ultima, la pestaña comun
     * "Rangos de Tablas". Las instancias no se destruyen: un modulo
     * deshabilitado simplemente sale de la vista y vuelve tal como estaba si se
     * habilita de nuevo.
     */
    private void aplicarModulos() {
        java.util.List<String> modulos = prefs.getModulos();

        tabs.removeAll();
        if (modulos.contains(Preferencias.MODULO_01)) {
            tabs.addTab("01 · Mensaje / Intentos / Tablas", c01.getContentPane());
        }
        if (modulos.contains(Preferencias.MODULO_02)) {
            tabs.addTab("02 · Tablas / Editar", c02.getContentPane());
        }
        if (modulos.contains(Preferencias.MODULO_03)) {
            tabs.addTab("03 · Figuras / Letras (completo)", c03.getContentPane());
        }
        if (rangos != null && modulos.contains(Preferencias.MODULO_RANGOS)) {
            tabs.addTab(TAB_RANGOS, rangos);
        }

        tabs.revalidate();
        tabs.repaint();
        if (isShowing()) {
            pack();
        }
    }

    // =====================================================================
    // Utilidades para reacomodar las pestañas internas
    // =====================================================================

    /**
     * Saca del JTabbedPane interno de una variante la pestaña con el titulo
     * indicado y devuelve su contenido, o null si no la tiene.
     */
    private static Component quitarTab(javax.swing.JFrame ventana, String titulo) {
        JTabbedPane interno = buscarTabbedPane(ventana.getContentPane());
        if (interno == null) {
            return null;
        }
        for (int i = 0; i < interno.getTabCount(); i++) {
            if (titulo.equals(interno.getTitleAt(i))) {
                Component contenido = interno.getComponentAt(i);
                interno.removeTabAt(i);
                return contenido;
            }
        }
        return null;
    }

    /** Primer JTabbedPane que aparece en la jerarquia, recorrida en anchura. */
    private static JTabbedPane buscarTabbedPane(Container raiz) {
        if (raiz == null) {
            return null;
        }
        for (Component hijo : raiz.getComponents()) {
            if (hijo instanceof JTabbedPane) {
                return (JTabbedPane) hijo;
            }
        }
        for (Component hijo : raiz.getComponents()) {
            if (hijo instanceof Container) {
                JTabbedPane encontrado = buscarTabbedPane((Container) hijo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
