package Vista;

import Controlador.PreferenciasGenerador;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

/**
 * Ventana oculta de configuracion del Generador. Se abre con el gesto
 * Ctrl+Shift+DobleClic sobre el rotulo "TABLAS A GENERAR" y permite elegir que
 * modos de generacion quedan disponibles para el operador: solo Normal, solo
 * Personalizada o ambas.
 *
 * La eleccion se guarda en el archivo binario de preferencias
 * (/Bingo/db/config.ker) para recordarla entre ejecuciones.
 *
 * @author oracle
 */
public class OpcionesGeneracion extends JDialog {

    private final PreferenciasGenerador prefs;

    private final JRadioButton rbNormal = new JRadioButton("Solo Normal");
    private final JRadioButton rbPersonalizada = new JRadioButton("Solo Personalizada");
    private final JRadioButton rbAmbas = new JRadioButton("Ambas (Normal y Personalizada)");

    private boolean aceptado = false;

    public OpcionesGeneracion(java.awt.Frame padre, PreferenciasGenerador prefs) {
        super(padre, "Opciones de Generacion de Tablas", true);
        this.prefs = prefs;

        JLabel titulo = new JLabel("Modos de generacion disponibles", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 14f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        JLabel ayuda = new JLabel("<html><center>Solo las opciones marcadas aqui se le muestran<br>"
                + "al operador en la pantalla del Generador.</center></html>", SwingConstants.CENTER);
        ayuda.setForeground(Color.DARK_GRAY);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(ayuda, BorderLayout.CENTER);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbNormal);
        grupo.add(rbPersonalizada);
        grupo.add(rbAmbas);

        JPanel opciones = new JPanel(new GridLayout(0, 1, 0, 4));
        opciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12))));
        opciones.add(rbNormal);
        opciones.add(rbPersonalizada);
        opciones.add(rbAmbas);

        seleccionarSegunPreferencias();

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

        JLabel ruta = new JLabel(PreferenciasGenerador.getRutaArchivo(), SwingConstants.CENTER);
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
        setMinimumSize(new Dimension(360, 0));
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void seleccionarSegunPreferencias() {
        String modos = prefs.getModosDisponibles();
        if (PreferenciasGenerador.MODO_NORMAL.equals(modos)) {
            rbNormal.setSelected(true);
        } else if (PreferenciasGenerador.MODO_PERSONALIZADA.equals(modos)) {
            rbPersonalizada.setSelected(true);
        } else {
            rbAmbas.setSelected(true);
        }
    }

    private void guardar() {
        String modo = PreferenciasGenerador.MODO_AMBAS;
        if (rbNormal.isSelected()) {
            modo = PreferenciasGenerador.MODO_NORMAL;
        } else if (rbPersonalizada.isSelected()) {
            modo = PreferenciasGenerador.MODO_PERSONALIZADA;
        }

        prefs.setModosDisponibles(modo);
        if (!prefs.guardar()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo escribir el archivo de preferencias:\n"
                    + PreferenciasGenerador.getRutaArchivo()
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
