package Vista;

import Controlador.Preferencias;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Pestaña "Mantenimiento": permite borrar del disco /Bingo/db/config.ker, el
 * archivo UNICO de configuracion. Desde la unificacion ese archivo guarda tanto
 * la partida programada (antes en config.dat) como las preferencias del
 * configurador y del Generador Universal, asi que borrarlo deja a los tres
 * programas en sus valores por defecto.
 *
 * Es un modulo mas de la tira de pestañas: se muestra u oculta desde la ventana
 * oculta de modulos (clave MT en Controlador/Preferencias). Conviene dejarlo
 * DESHABILITADO en las entregas a cliente.
 *
 * El estado que muestra no se cachea: Vista/Config llama a refrescarEstado()
 * cada vez que se entra a esta pestaña, porque el archivo puede aparecer o
 * desaparecer por fuera de esta ventana (lo escribe tambien el Generador
 * Universal, o alguien lo borra a mano).
 *
 * @author oracle
 */
public class Mantenimiento extends JPanel {

    private final Preferencias prefs;

    /** Que hacer despues de borrar: rearmar la tira de pestañas. */
    private final Runnable alCambiar;

    private final JLabel lblEstado = new JLabel("", SwingConstants.CENTER);
    private final JButton btnEliminar = new JButton("Eliminar config.ker");

    public Mantenimiento(Preferencias prefs, Runnable alCambiar) {
        this.prefs = prefs;
        this.alCambiar = alCambiar;

        JLabel titulo = new JLabel("Mantenimiento de preferencias", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));

        JLabel ayuda = new JLabel("<html><center>Borra del disco el archivo unico de configuracion.<br>"
                + "Se pierde <b>TODO</b> lo que guarda:<br>"
                + "&bull; la <b>partida programada</b> (intentos, mensaje y tablas ganadoras)<br>"
                + "&bull; los modulos visibles del configurador<br>"
                + "&bull; el modo de generacion del <b>Generador Universal</b><br>"
                + "Los tres programas vuelven a sus valores por defecto.</center></html>",
                SwingConstants.CENTER);
        ayuda.setForeground(Color.DARK_GRAY);

        JLabel ruta = new JLabel(Preferencias.getRutaArchivo(), SwingConstants.CENTER);
        ruta.setFont(ruta.getFont().deriveFont(Font.PLAIN, 11f));
        ruta.setForeground(Color.GRAY);

        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD, 12f));

        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminar();
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnEliminar);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        for (java.awt.Component c : new java.awt.Component[]{
            titulo, Box.createVerticalStrut(14), ayuda, Box.createVerticalStrut(18),
            ruta, Box.createVerticalStrut(6), lblEstado, Box.createVerticalStrut(18), botones}) {
            if (c instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent) c).setAlignmentX(CENTER_ALIGNMENT);
            }
            centro.add(c);
        }

        setLayout(new BorderLayout());
        add(centro, BorderLayout.CENTER);
        setPreferredSize(new Dimension(560, 320));

        refrescarEstado();
    }

    /** Deja el rotulo y el boton acordes a lo que hay en disco. */
    public final void refrescarEstado() {
        if (Preferencias.existeArchivo()) {
            lblEstado.setText("El archivo existe (" + Preferencias.tamanoArchivo() + " bytes)");
            lblEstado.setForeground(new Color(0, 120, 0));
            btnEliminar.setEnabled(true);
        } else {
            lblEstado.setText("El archivo no existe: rigen los valores por defecto");
            lblEstado.setForeground(Color.GRAY);
            btnEliminar.setEnabled(false);
        }
    }

    private void eliminar() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "<html>Se borrara del disco:<br><b>" + Preferencias.getRutaArchivo() + "</b>"
                + "<br><br>Se pierde la <b>partida programada</b> completa, los modulos"
                + "<br>visibles del configurador y el modo de generacion del"
                + "<br><b>Generador Universal</b>: es el archivo unico de los tres."
                + "<br><br>¿Confirma?</html>",
                "Eliminar preferencias", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        if (!Preferencias.eliminarArchivo()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo borrar el archivo. Revise los permisos de:\n"
                    + Preferencias.getRutaArchivo(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            refrescarEstado();
            return;
        }

        // El objeto en memoria conserva lo que ya habia leido; se recarga para
        // que quede en sintonia con el disco (ahora vacio = valores por defecto).
        prefs.cargar();
        refrescarEstado();

        JOptionPane.showMessageDialog(this,
                "Preferencias eliminadas. Se restablecieron los valores por defecto.",
                "Listo", JOptionPane.INFORMATION_MESSAGE);

        // Rearmar la tira despues de este evento: aplicarModulos() quita y vuelve
        // a poner las pestañas, incluida la que dispara este clic.
        if (alCambiar != null) {
            SwingUtilities.invokeLater(alCambiar);
        }
    }
}
