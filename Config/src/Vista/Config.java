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
 * habilita cualquier combinacion de 01, 02, 03, "Rangos de Tablas" y
 * "Mantenimiento". La eleccion se guarda en el archivo binario
 * /Bingo/db/config.ker (ver Controlador/Preferencias).
 *
 * Al presionar cualquier boton de 01, 02 o 03 se garantiza que ese archivo
 * exista (se crea con la seleccion vigente si falta); al entrar a
 * "Mantenimiento" se vuelve a mirar el disco. Ver engancharCreacionPreferencias()
 * y alEntrarAPestaña(...).
 */
package Vista;

import Controlador.Preferencias;
import java.awt.Component;
import java.io.IOException;
import java.sql.SQLException;

public class Config extends javax.swing.JFrame {

    /** Titulo de la pestaña interna que se promueve a pestaña principal. */
    private static final String TAB_RANGOS = "Rangos de Tablas";

    private javax.swing.JTabbedPane tabs;

    /** Pestaña "Rangos de Tablas", hoy una clase propia. */
    private RangosTablas rangos;

    /** Pestaña propia de esta ventana (no viene de ninguna variante). */
    private Mantenimiento mantenimiento;

    /** Pestaña de figuras, generada desde matriz.txt (no del formulario). */
    private FigurasDinamicas figuras;

    private Preferencias prefs;

    public Config() throws IOException, SQLException {
        rangos = new RangosTablas();

        prefs = new Preferencias();

        tabs = new javax.swing.JTabbedPane();
        // Se crea antes de armar la tira, y una sola vez: al borrar el archivo
        // pide rearmar las pestañas, porque vuelven los valores por defecto.
        mantenimiento = new Mantenimiento(prefs, new Runnable() {
            @Override
            public void run() {
                aplicarModulos();
            }
        });
        figuras = new FigurasDinamicas(prefs);
        aplicarModulos();

        // Gesto oculto: Ctrl+Shift+DobleClic sobre la tira de pestañas abre la
        // ventana de modulos. (Dentro del contenido de una pestaña el clic lo
        // atiende el control que este debajo, por eso el gesto va aqui arriba.)
        tabs.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.isControlDown() && e.isShiftDown()) {
                    abrirOpcionesModulos();
                    return;
                }
                // Clic sobre la solapa de Mantenimiento: revisar el disco aunque
                // la pestaña ya estuviera seleccionada (ahi no hay cambio de
                // seleccion y el ChangeListener no se entera).
                int i = tabs.indexAtLocation(e.getX(), e.getY());
                if (i >= 0) {
                    alEntrarAPestaña(tabs.getComponentAt(i));
                }
            }
        });

        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                alEntrarAPestaña(tabs.getSelectedComponent());
            }
        });

        // El ChangeListener no corre para la pestaña que ya quedo seleccionada
        // al armar la tira, asi que se atiende a mano.
        alEntrarAPestaña(tabs.getSelectedComponent());

        setTitle("Configuración Universal");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setContentPane(tabs);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Al entrar a "Mantenimiento" (por cambio de seleccion o por clic sobre la
     * solapa, incluso si ya estaba seleccionada) se vuelve a mirar el disco,
     * sin cachear nada.
     */
    private void alEntrarAPestaña(Component pestaña) {
        if (pestaña == mantenimiento && pestaña != null) {
            mantenimiento.refrescarEstado();
        }
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
     * Deja como pestañas solo los modulos habilitados en las preferencias, en
     * el orden "Figuras (todas)", "Rangos de Tablas" y, de ultima,
     * "Mantenimiento": primero lo que se configura para jugar, despues las
     * tablas y al final el borrado. Las instancias no se destruyen: un modulo
     * deshabilitado sale de la vista y vuelve tal como estaba si se habilita
     * de nuevo.
     */
    private void aplicarModulos() {
        java.util.List<String> modulos = prefs.getModulos();

        tabs.removeAll();
        if (modulos.contains(Preferencias.MODULO_FIGURAS)) {
            tabs.addTab("Figuras (todas)", figuras);
        }
        if (rangos != null && modulos.contains(Preferencias.MODULO_RANGOS)) {
            tabs.addTab(TAB_RANGOS, rangos);
        }
        if (modulos.contains(Preferencias.MODULO_MANTENIMIENTO)) {
            mantenimiento.refrescarEstado();
            tabs.addTab("Mantenimiento", mantenimiento);
        }

        tabs.revalidate();
        tabs.repaint();
        if (isShowing()) {
            pack();
        }
    }

}
