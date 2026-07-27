package Vista;

import Controlador.Preferencias;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana oculta de configuracion del Config Universal. Se abre con el gesto
 * Ctrl+Shift+DobleClic sobre la tira de pestañas y permite habilitar cualquier
 * combinacion de los modulos opcionales (01, 02, 03, "Rangos de Tablas" y
 * "Mantenimiento"): solo los marcados quedan visibles como pestañas.
 *
 * La eleccion se guarda en el archivo binario de preferencias
 * (/Bingo/db/config.ker), el mismo que usa el Generador Universal.
 *
 * @author oracle
 */
public class OpcionesConfig extends JDialog {

    private final Preferencias prefs;

    private final JCheckBox ch01 = new JCheckBox("01 · Mensaje / Intentos / Tablas");
    private final JCheckBox ch02 = new JCheckBox("02 · Tablas / Editar");
    private final JCheckBox ch03 = new JCheckBox("03 · Figuras / Letras (completo)");
    private final JCheckBox chRG = new JCheckBox("Rangos de Tablas");
    private final JCheckBox chMT = new JCheckBox("Mantenimiento (eliminar config.ker)");

    private boolean aceptado = false;

    public OpcionesConfig(java.awt.Frame padre, Preferencias prefs) {
        super(padre, "Modulos del Configurador", true);
        this.prefs = prefs;

        JLabel titulo = new JLabel("Modulos disponibles", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        JLabel ayuda = new JLabel("<html><center>Solo los modulos marcados aqui se muestran<br>"
                + "como pestañas en la ventana del configurador.</center></html>",
                SwingConstants.CENTER);
        ayuda.setForeground(Color.DARK_GRAY);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(ayuda, BorderLayout.CENTER);

        JPanel opciones = new JPanel(new GridLayout(0, 1, 0, 4));
        opciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12))));
        opciones.add(ch01);
        opciones.add(ch02);
        opciones.add(ch03);
        opciones.add(chRG);
        opciones.add(chMT);

        marcarSegunPreferencias();

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar();
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        JLabel ruta = new JLabel(Preferencias.getRutaArchivo(), SwingConstants.CENTER);
        ruta.setFont(ruta.getFont().deriveFont(Font.PLAIN, 10f));
        ruta.setForeground(Color.GRAY);
        ruta.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.CENTER);
        sur.add(ruta, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(norte, BorderLayout.NORTH);
        add(opciones, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnGuardar);
        setMinimumSize(new Dimension(380, 0));
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void marcarSegunPreferencias() {
        ch01.setSelected(prefs.tieneModulo(Preferencias.MODULO_01));
        ch02.setSelected(prefs.tieneModulo(Preferencias.MODULO_02));
        ch03.setSelected(prefs.tieneModulo(Preferencias.MODULO_03));
        chRG.setSelected(prefs.tieneModulo(Preferencias.MODULO_RANGOS));
        chMT.setSelected(prefs.tieneModulo(Preferencias.MODULO_MANTENIMIENTO));
    }

    private void guardar() {
        List<String> elegidos = new ArrayList<>();
        if (ch01.isSelected()) {
            elegidos.add(Preferencias.MODULO_01);
        }
        if (ch02.isSelected()) {
            elegidos.add(Preferencias.MODULO_02);
        }
        if (ch03.isSelected()) {
            elegidos.add(Preferencias.MODULO_03);
        }
        if (chRG.isSelected()) {
            elegidos.add(Preferencias.MODULO_RANGOS);
        }
        if (chMT.isSelected()) {
            elegidos.add(Preferencias.MODULO_MANTENIMIENTO);
        }

        if (elegidos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe dejar habilitado al menos un modulo.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        prefs.setModulos(elegidos);
        if (!prefs.guardar()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo escribir el archivo de preferencias:\n"
                    + Preferencias.getRutaArchivo()
                    + "\n\nEl cambio se aplica ahora, pero no se recordara.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        aceptado = true;
        dispose();
    }

    /** true si el usuario guardo (para que la ventana principal se refresque). */
    public boolean isAceptado() {
        return aceptado;
    }
}
