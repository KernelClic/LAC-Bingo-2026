/*
 * Config Universal — unifica en una sola ventana las tres variantes del
 * configurador (01, 02, 03) mediante pestañas (JTabbedPane). Cada pestaña
 * reutiliza integramente la UI y la logica de la variante correspondiente
 * (se reparenta su contentPane). Se quito el cambio de contraseña; la
 * validacion por licencia se aplica en Entrada (node-lock, Controlador.Licencia).
 */
package Vista;

import java.io.IOException;
import java.sql.SQLException;

public class Config extends javax.swing.JFrame {

    // Se conservan las referencias vivas para que sus listeners sigan operando
    // aunque su contentPane se muestre dentro de una pestaña de esta ventana.
    private Config01 c01;
    private Config02 c02;
    private Config03 c03;
    private javax.swing.JTabbedPane tabs;

    public Config() throws IOException, SQLException {
        c01 = new Config01();
        c02 = new Config02();
        c03 = new Config03();

        tabs = new javax.swing.JTabbedPane();
        tabs.addTab("01 · Mensaje / Intentos / Tablas", c01.getContentPane());
        tabs.addTab("02 · Tablas / Editar", c02.getContentPane());
        tabs.addTab("03 · Figuras / Letras (completo)", c03.getContentPane());

        setTitle("Configuración Universal — KernelClic");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setContentPane(tabs);
        pack();
        setLocationRelativeTo(null);
    }
}
