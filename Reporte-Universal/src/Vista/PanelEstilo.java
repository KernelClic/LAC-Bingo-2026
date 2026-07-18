package Vista;

import Controlador.GestorFuentes;
import Modelo.EstiloTexto;

import javax.swing.*;
import java.awt.*;

/**
 * Control Swing reutilizable para configurar el formato de un elemento de texto:
 * fuente, negrita, cursiva, tamaño, color de letra y color de fondo (resaltado).
 *
 * Las fuentes se toman de {@link GestorFuentes} (SO + /Bingo/Fuentes).
 */
public class PanelEstilo extends JPanel {

    private final JComboBox<String> cmbFuente;
    private final JCheckBox         chkNegrita;
    private final JCheckBox         chkCursiva;
    private final JSpinner          spnTamaño;
    private final JButton           btnColor;
    private final JCheckBox         chkFondo;
    private final JButton           btnFondo;

    private final boolean conFuente;   // los QR no usan tipografía

    public PanelEstilo(EstiloTexto def) {
        this(def, true, 5, 72);
    }

    /**
     * @param def       estilo inicial
     * @param conFuente si false, oculta el combo de fuente y los checks (para QR)
     * @param min,max   rango del spinner de tamaño
     */
    public PanelEstilo(EstiloTexto def, boolean conFuente, int min, int max) {
        super(new FlowLayout(FlowLayout.LEFT, 4, 2));
        this.conFuente = conFuente;

        cmbFuente = new JComboBox<>(GestorFuentes.get().listarFuentes());
        cmbFuente.setSelectedItem(def.fuente);
        if (cmbFuente.getSelectedItem() == null)
            cmbFuente.setSelectedItem(GestorFuentes.get().fuentePorDefecto());
        cmbFuente.setPrototypeDisplayValue("WWWWWWWWWWWWWW");

        chkNegrita = new JCheckBox("N", def.negrita);
        chkNegrita.setFont(chkNegrita.getFont().deriveFont(Font.BOLD));
        chkNegrita.setToolTipText("Negrita");
        chkCursiva = new JCheckBox("C", def.cursiva);
        chkCursiva.setFont(chkCursiva.getFont().deriveFont(Font.ITALIC));
        chkCursiva.setToolTipText("Cursiva");

        int tamInicial = Math.round(def.tamaño);
        spnTamaño = new JSpinner(new SpinnerNumberModel(
                Math.max(min, Math.min(max, tamInicial)), min, max, 1));
        ((JSpinner.DefaultEditor) spnTamaño.getEditor()).getTextField().setColumns(3);

        btnColor = botonColor(def.color != null ? def.color : Color.BLACK, "Color de letra");

        chkFondo = new JCheckBox("Fondo", def.fondo != null);
        chkFondo.setToolTipText("Resaltado detrás del texto");
        btnFondo = botonColor(def.fondo != null ? def.fondo : Color.YELLOW, "Color de fondo");
        btnFondo.setEnabled(def.fondo != null);
        chkFondo.addActionListener(e -> btnFondo.setEnabled(chkFondo.isSelected()));

        if (conFuente) {
            add(cmbFuente);
            add(chkNegrita);
            add(chkCursiva);
        }
        add(new JLabel("pt:"));
        add(spnTamaño);
        add(new JLabel("Letra:"));
        add(btnColor);
        add(chkFondo);
        add(btnFondo);
    }

    /** Construye el {@link EstiloTexto} a partir de los controles. */
    public EstiloTexto getEstilo() {
        EstiloTexto e = new EstiloTexto();
        e.fuente  = conFuente ? (String) cmbFuente.getSelectedItem()
                              : GestorFuentes.get().fuentePorDefecto();
        e.negrita = conFuente && chkNegrita.isSelected();
        e.cursiva = conFuente && chkCursiva.isSelected();
        e.tamaño  = ((Number) spnTamaño.getValue()).floatValue();
        e.color   = btnColor.getBackground();
        e.fondo   = chkFondo.isSelected() ? btnFondo.getBackground() : null;
        return e;
    }

    private JButton botonColor(Color inicial, String tooltip) {
        JButton btn = new JButton("  ");
        btn.setBackground(inicial);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(34, 20));
        btn.setToolTipText(tooltip);
        btn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, tooltip, btn.getBackground());
            if (c != null) btn.setBackground(c);
        });
        return btn;
    }
}
