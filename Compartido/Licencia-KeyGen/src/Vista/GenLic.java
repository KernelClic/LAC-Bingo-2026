package Vista;

import Controlador.Licencia;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Generador de claves de activacion — HERRAMIENTA INTERNA.
 *
 * <p>Convierte el ID DE EQUIPO que reporta el cliente en la CLAVE DE ACTIVACION
 * de ese equipo. Comparte el secreto con {@link Controlador.Licencia}, de modo
 * que la clave solo sirve en el PC cuyo ID se uso para generarla.</p>
 *
 * <p><b>No debe entregarse al cliente</b>: quien tiene este programa puede
 * activar cualquier equipo.</p>
 */
public class GenLic extends JFrame {

    private final JTextField txtId = new JTextField(22);
    private final JTextField txtClave = new JTextField(22);

    public GenLic() {
        super("Generador de Claves de Activación (uso interno)");

        JLabel titulo = new JLabel("Generador de claves de activación", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));

        JLabel ayuda = new JLabel("<html><center>Pegue el <b>ID de equipo</b> que le reporto el cliente<br>"
                + "y entreguele la clave resultante. Solo sirve en ese equipo.</center></html>",
                SwingConstants.CENTER);
        ayuda.setForeground(Color.DARK_GRAY);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(ayuda, BorderLayout.CENTER);
        norte.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        txtClave.setEditable(false);
        txtClave.setFont(txtClave.getFont().deriveFont(Font.BOLD, 14f));
        txtId.setFont(txtId.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel campos = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(6, 8, 6, 8);
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; campos.add(new JLabel("ID de equipo:"), c);
        c.gridx = 1; campos.add(txtId, c);
        c.gridx = 0; c.gridy = 1; campos.add(new JLabel("Clave:"), c);
        c.gridx = 1; campos.add(txtClave, c);
        campos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 16, 8, 16),
                BorderFactory.createEtchedBorder()));

        JButton btnGenerar = new JButton("Generar clave");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                generar();
            }
        });
        JButton btnCopiar = new JButton("Copiar clave");
        btnCopiar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copiar();
            }
        });
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(btnGenerar);
        botones.add(btnCopiar);
        botones.add(btnSalir);

        JLabel pie = new JLabel("Herramienta interna: no entregar al cliente", SwingConstants.CENTER);
        pie.setFont(pie.getFont().deriveFont(Font.PLAIN, 10f));
        pie.setForeground(Color.GRAY);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 6, 8, 6));

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.CENTER);
        sur.add(pie, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(norte, BorderLayout.NORTH);
        add(campos, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnGenerar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(430, 0));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void generar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba el ID de equipo del cliente.",
                    "Falta el ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtClave.setText(Licencia.generarClaveDesdeId(id));
    }

    private void copiar() {
        String clave = txtClave.getText().trim();
        if (clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero genere la clave.",
                    "Nada que copiar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(clave), null);
        JOptionPane.showMessageDialog(this, "Clave copiada al portapapeles.",
                "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Modo consola:  java -jar Bingo_GenLic.jar <ID-DE-EQUIPO>
        if (args.length > 0 && !args[0].startsWith("-")) {
            System.out.println(Licencia.generarClaveDesdeId(args[0].trim()));
            return;
        }
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // se sigue con el aspecto por defecto
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GenLic().setVisible(true);
            }
        });
    }
}
