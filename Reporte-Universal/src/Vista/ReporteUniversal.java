package Vista;

import Controlador.AccesoAleatorio;
import Controlador.ConectorSqlite;
import Controlador.ConsultaTablas;
import Controlador.GeneradorPDF;
import Controlador.Licencia;
import Modelo.ConfigReporte;
import Modelo.RangoPremio;
import Modelo.Tabla;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;

public class ReporteUniversal extends JFrame {

    private final ConfigReporte def = new ConfigReporte();   // valores por defecto

    // ---- Frente ----
    private JTextField tfTitulo;
    private PanelEstilo peTitulo, peNumeros, peBoleta;
    private JComboBox<String> cmbDigitos;
    private JButton  btnColorMarco;
    private JSpinner spnAnchoMarco;
    private JCheckBox chkMarco;

    // ---- Reverso ----
    private JTextField tfTituloReverso;
    private JTextArea  taTextoReverso;
    private PanelEstilo peTextoRev;

    // ---- Datos comunes ----
    private JComboBox<String> cmbTamañoPagina;
    private JTextField tfFecha;
    private JComboBox<String> cmbFechaDestino;
    private PanelEstilo peFecha;
    private JTextField tfValor;
    private JComboBox<String> cmbValorDestino;
    private PanelEstilo peValor;
    private JTextField tfCaducidad;
    private JComboBox<String> cmbCaducidadDestino;

    // ---- Extras ----
    private JTextField tfMarcaAgua;
    private JLabel     lblPreviewMarca;
    private JComboBox<String> cmbMarcaAguaDest;
    private JSlider    sldOpacidad;
    private JLabel     lblOpacidad;
    private JCheckBox  chkQR;
    private PanelEstilo peQR;

    // ---- Premios (QR Ganador) ----
    private JCheckBox  chkQRGanador;
    private JTextField tfMensajeDefecto;
    private DefaultTableModel premiosModel;
    private JTable     tblPremios;

    // =====================================================================

    public ReporteUniversal() throws IOException {
        super("Bingo — Reporte Universal");
        if (!AccesoAleatorio.buscarFile(
                new File(AccesoAleatorio.getRutaFileDB() + "tablas.db"))) {
            JOptionPane.showMessageDialog(null,
                    "Error de conexión a base de datos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    // =====================================================================
    // Construcción de interfaz
    // =====================================================================

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Frente",  buildTabFrente());
        tabs.addTab("Reverso", buildTabReverso());
        tabs.addTab("Datos",   buildTabDatos());
        tabs.addTab("Extras",  buildTabExtras());
        tabs.addTab("Premios (QR Ganador)", buildTabPremios());

        setLayout(new BorderLayout(6, 6));
        add(tabs,                BorderLayout.CENTER);
        add(buildPanelBotones(), BorderLayout.SOUTH);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    // ---- Tab Frente ----
    private JPanel buildTabFrente() {
        tfTitulo  = new JTextField("BINGO", 8);
        peTitulo  = new PanelEstilo(def.estiloTitulo,  true, 10, 60);
        peNumeros = new PanelEstilo(def.estiloNumeros, true, 8, 40);
        peBoleta  = new PanelEstilo(def.estiloBoleta,  true, 8, 30);

        cmbDigitos = new JComboBox<>(new String[]{"4 cifras (0000)", "5 cifras (00000)"});
        cmbDigitos.setSelectedIndex(def.digitosBoleta - 4);

        btnColorMarco = botonColor(Color.BLACK, "Color del marco");
        spnAnchoMarco = new JSpinner(new SpinnerNumberModel(1.5, 0.5, 6.0, 0.5));
        ((JSpinner.DefaultEditor) spnAnchoMarco.getEditor()).getTextField().setColumns(4);
        chkMarco = new JCheckBox("Imprimir marco", true);

        JPanel p = panel("Configuración del Frente");
        GridBagConstraints c = gbc();
        fila(p, c, 0, "Título (5 chars):", tfTitulo);
        fila(p, c, 1, "Formato título:",   peTitulo);
        fila(p, c, 2, "Formato números:",  peNumeros);
        fila(p, c, 3, "Cifras boleta:",    cmbDigitos);
        fila(p, c, 4, "Formato boleta:",   peBoleta);
        fila(p, c, 5, "Color marco:",      btnColorMarco);
        fila(p, c, 6, "Ancho líneas (pt):", spnAnchoMarco);
        filaComp(p, c, 7, chkMarco);
        return p;
    }

    // ---- Tab Reverso ----
    private JPanel buildTabReverso() {
        tfTituloReverso = new JTextField("BINGO", 12);
        taTextoReverso  = new JTextArea(def.textoReverso, 9, 30);
        taTextoReverso.setLineWrap(true);
        taTextoReverso.setWrapStyleWord(true);
        peTextoRev = new PanelEstilo(def.estiloTextoReverso, true, 5, 20);

        JPanel p = panel("Configuración del Reverso");
        GridBagConstraints c = gbc();
        fila(p, c, 0, "Título reverso:", tfTituloReverso);
        c.gridy = 1; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Texto reverso:"), c);
        c.gridx = 1; c.weightx = 1;
        p.add(new JScrollPane(taTextoReverso), c);
        fila(p, c, 2, "Formato texto:", peTextoRev);
        return p;
    }

    // ---- Tab Datos ----
    private JPanel buildTabDatos() {
        cmbTamañoPagina = new JComboBox<>(new String[]{"Carta (8.5\" × 11\")", "Oficio (8.5\" × 14\")"});
        cmbTamañoPagina.setSelectedIndex(1);

        tfFecha         = new JTextField("DD/MM/YYYY", 12);
        cmbFechaDestino = comboDest();
        peFecha         = new PanelEstilo(def.estiloFecha, true, 5, 20);

        tfValor         = new JTextField("$ 1.000", 12);
        cmbValorDestino = comboDest();
        cmbValorDestino.setSelectedIndex(ConfigReporte.DEST_REVERSO);
        peValor         = new PanelEstilo(def.estiloValor, true, 5, 20);

        tfCaducidad         = new JTextField("15", 6);
        cmbCaducidadDestino = comboDest();
        cmbCaducidadDestino.setSelectedIndex(ConfigReporte.DEST_REVERSO);

        JPanel p = panel("Datos del Juego");
        GridBagConstraints c = gbc();
        fila(p, c, 0, "Tamaño de página:",   cmbTamañoPagina);
        fila(p, c, 1, "Fecha (DD/MM/YYYY):", tfFecha);
        fila(p, c, 2, "Imprimir fecha en:",  cmbFechaDestino);
        fila(p, c, 3, "Formato fecha:",      peFecha);
        fila(p, c, 4, "Valor del cartón:",   tfValor);
        fila(p, c, 5, "Imprimir valor en:",  cmbValorDestino);
        fila(p, c, 6, "Formato valor:",      peValor);
        fila(p, c, 7, "Caducidad (días):",   tfCaducidad);
        fila(p, c, 8, "Imprimir caducidad en:", cmbCaducidadDestino);
        return p;
    }

    // ---- Tab Extras ----
    private JPanel buildTabExtras() {
        tfMarcaAgua = new JTextField(16);
        tfMarcaAgua.setEditable(false);

        lblPreviewMarca = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblPreviewMarca.setPreferredSize(new Dimension(140, 140));
        lblPreviewMarca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Vista previa"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        lblPreviewMarca.setHorizontalTextPosition(SwingConstants.CENTER);
        lblPreviewMarca.setVerticalTextPosition(SwingConstants.BOTTOM);

        JButton btnExaminar = new JButton("Examinar...");
        btnExaminar.addActionListener(e -> elegirImagen());
        JButton btnLimpiar = new JButton("Quitar");
        btnLimpiar.addActionListener(e -> {
            tfMarcaAgua.setText("");
            lblPreviewMarca.setIcon(null);
            lblPreviewMarca.setText("Sin imagen");
        });

        cmbMarcaAguaDest = comboDest();
        cmbMarcaAguaDest.setSelectedIndex(ConfigReporte.DEST_AMBOS);

        int op0 = Math.round(def.marcaAguaOpacidad * 100);
        sldOpacidad = new JSlider(0, 100, op0);
        sldOpacidad.setMajorTickSpacing(25);
        sldOpacidad.setPaintTicks(true);
        lblOpacidad = new JLabel(op0 + " %");
        sldOpacidad.addChangeListener(e -> lblOpacidad.setText(sldOpacidad.getValue() + " %"));
        JPanel pOp = new JPanel(new BorderLayout(6, 0));
        pOp.add(sldOpacidad, BorderLayout.CENTER);
        pOp.add(lblOpacidad, BorderLayout.EAST);

        chkQR = new JCheckBox("Generar QR de Seguridad (solo en el frente)", false);
        peQR = new PanelEstilo(def.estiloQR, false, 50, 150);   // sin tipografía

        JPanel pFile = new JPanel(new BorderLayout(4, 0));
        pFile.add(tfMarcaAgua, BorderLayout.CENTER);
        JPanel pBtns = new JPanel(new GridLayout(1, 2, 4, 0));
        pBtns.add(btnExaminar);
        pBtns.add(btnLimpiar);
        pFile.add(pBtns, BorderLayout.EAST);

        JPanel pLeft = panel("Marca de Agua y QR");
        GridBagConstraints c = gbc();
        c.gridy = 0; c.gridx = 0; c.weightx = 0;
        pLeft.add(new JLabel("Imagen:"), c);
        c.gridx = 1; c.weightx = 1;
        pLeft.add(pFile, c);
        fila(pLeft, c, 1, "Imprimir marca en:", cmbMarcaAguaDest);
        fila(pLeft, c, 2, "Transparencia:",     pOp);
        filaComp(pLeft, c, 3, chkQR);
        fila(pLeft, c, 4, "Formato QR (color/fondo):", peQR);

        JPanel main = new JPanel(new BorderLayout(8, 0));
        main.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        main.add(pLeft,           BorderLayout.CENTER);
        main.add(lblPreviewMarca, BorderLayout.EAST);
        return main;
    }

    // ---- Tab Premios (QR Ganador) ----
    private JPanel buildTabPremios() {
        chkQRGanador = new JCheckBox(
                "Generar QR Ganador (solo reverso, en TODAS las tablas)", false);
        tfMensajeDefecto = new JTextField(def.mensajePremioDefecto, 28);
        tfMensajeDefecto.setEnabled(false);
        chkQRGanador.addActionListener(e -> tfMensajeDefecto.setEnabled(chkQRGanador.isSelected()));

        premiosModel = new DefaultTableModel(
                new Object[]{"Desde", "Hasta", "Mensaje de premio", "Fecha de creación"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col != 3; }
            @Override public Class<?> getColumnClass(int col) {
                return (col == 0 || col == 1) ? Integer.class : String.class;
            }
        };
        tblPremios = new JTable(premiosModel);
        tblPremios.setRowHeight(22);
        tblPremios.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblPremios.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblPremios.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblPremios.getColumnModel().getColumn(3).setPreferredWidth(110);

        JButton btnAgregar = new JButton("Agregar rango");
        btnAgregar.addActionListener(e -> premiosModel.addRow(
                new Object[]{0, 0, "", new SimpleDateFormat("dd/MM/yyyy").format(new Date())}));
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.addActionListener(e -> {
            int r = tblPremios.getSelectedRow();
            if (r >= 0) {
                if (tblPremios.isEditing()) tblPremios.getCellEditor().stopCellEditing();
                premiosModel.removeRow(r);
            }
        });

        JPanel pTop = new JPanel(new GridBagLayout());
        GridBagConstraints c = gbc();
        filaComp(pTop, c, 0, chkQRGanador);
        fila(pTop, c, 1, "Mensaje por defecto (sin premio):", tfMensajeDefecto);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pBtns.add(btnAgregar);
        pBtns.add(btnEliminar);

        JLabel ayuda = new JLabel(
                "<html><i>El QR Ganador se imprime en el reverso de <b>todas</b> las tablas. "
                + "Las que estén en un rango llevan su mensaje de premio; las demás, el "
                + "mensaje por defecto. Ej.: Desde 10, Hasta 12, \"Gana Moto\" premia 0010–0012; "
                + "para un solo cartón usa Desde=Hasta (p.ej. 234–234).</i></html>");

        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(tituloBorde("Premios sorpresa (QR Ganador)"));
        p.add(pTop, BorderLayout.NORTH);
        JPanel pCenter = new JPanel(new BorderLayout(4, 4));
        pCenter.add(new JScrollPane(tblPremios), BorderLayout.CENTER);
        pCenter.add(pBtns, BorderLayout.SOUTH);
        p.add(pCenter, BorderLayout.CENTER);
        p.add(ayuda,   BorderLayout.SOUTH);
        return p;
    }

    // ---- Panel de botones ----
    private JPanel buildPanelBotones() {
        JButton btnFrente  = new JButton("Generar Frente");
        JButton btnReverso = new JButton("Generar Reverso");
        JButton btnDoble   = new JButton("Doble Cara (1 PDF)");
        JButton btnDosPDF  = new JButton("Dos PDFs separados");
        JButton btnSalir   = new JButton("Salir");

        btnFrente .addActionListener(e -> generarPDF(ConfigReporte.MODO_FRENTE));
        btnReverso.addActionListener(e -> generarPDF(ConfigReporte.MODO_REVERSO));
        btnDoble  .addActionListener(e -> generarPDF(ConfigReporte.MODO_DOBLE_CARA));
        btnDosPDF .addActionListener(e -> generarPDF(ConfigReporte.MODO_DOS_PDF));
        btnSalir  .addActionListener(e -> System.exit(0));

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        p.add(btnFrente); p.add(btnReverso);
        p.add(btnDoble);  p.add(btnDosPDF);
        p.add(btnSalir);
        return p;
    }

    // =====================================================================
    // Generar PDF
    // =====================================================================

    private void generarPDF(int modo) {
        if (tblPremios.isEditing()) tblPremios.getCellEditor().stopCellEditing();
        ConfigReporte cfg;
        try {
            cfg = leerConfig();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Hay un valor inválido en los rangos de premio:\n" + ex.getMessage(),
                    "Datos de premios", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Avisar si alguna fuente elegida no se puede incrustar (se usará Helvetica).
        java.util.List<Modelo.EstiloTexto> estilos = java.util.Arrays.asList(
                cfg.estiloTitulo, cfg.estiloNumeros, cfg.estiloBoleta,
                cfg.estiloTextoReverso, cfg.estiloFecha, cfg.estiloValor);
        java.util.List<String> noEmb =
                Controlador.GestorFuentes.get().fuentesNoIncrustables(estilos);
        if (!noEmb.isEmpty()) {
            int r = JOptionPane.showConfirmDialog(this,
                    "Estas fuentes no se pueden incrustar en el PDF (OpenPDF no las soporta,\n"
                    + "p.ej. fuentes matemáticas OTF) y se reemplazarán por Helvetica:\n\n  • "
                    + String.join("\n  • ", noEmb)
                    + "\n\n¿Desea continuar con la sustitución?",
                    "Fuentes no compatibles", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
        }

        ConectorSqlite cn = new ConectorSqlite("", "",
                AccesoAleatorio.getRutaFileDB() + "tablas.db", "");
        List<Tabla> tablas;
        try {
            tablas = ConsultaTablas.getTablasActivas(cn.getConexion());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer las tablas:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            cn.Cerrar(); return;
        }
        if (tablas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay tablas activas en la base de datos.",
                    "Sin datos", JOptionPane.WARNING_MESSAGE);
            cn.Cerrar(); return;
        }

        if (modo == ConfigReporte.MODO_DOS_PDF) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Nombre base para los dos PDFs (sin extensión)");
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) { cn.Cerrar(); return; }
            String pb = sinExtension(fc.getSelectedFile().getAbsolutePath());
            ejecutar(() -> {
                GeneradorPDF.generarDosPDF(tablas, cfg, pb);
                mostrarExito("Archivos generados:\n" + pb + "_Frentes.pdf\n" + pb + "_Reversos.pdf");
            });
        } else {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Guardar PDF");
            fc.setSelectedFile(new File(nombreSugerido(modo)));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) { cn.Cerrar(); return; }
            File dest = asegurarPDF(fc.getSelectedFile());
            ejecutar(() -> {
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    switch (modo) {
                        case ConfigReporte.MODO_FRENTE:
                            GeneradorPDF.generarFrente(tablas, cfg, fos);    break;
                        case ConfigReporte.MODO_REVERSO:
                            GeneradorPDF.generarReverso(tablas, cfg, fos);   break;
                        default:
                            GeneradorPDF.generarDobleCara(tablas, cfg, fos);
                    }
                }
                mostrarExito("PDF generado:\n" + dest.getAbsolutePath());
            });
        }
        cn.Cerrar();
    }

    // =====================================================================
    // Leer configuración desde controles
    // =====================================================================

    private ConfigReporte leerConfig() {
        ConfigReporte cfg = new ConfigReporte();

        cfg.titulo        = tfTitulo.getText();
        cfg.estiloTitulo  = peTitulo.getEstilo();
        cfg.estiloNumeros = peNumeros.getEstilo();
        cfg.estiloBoleta  = peBoleta.getEstilo();
        cfg.digitosBoleta = cmbDigitos.getSelectedIndex() + 4;

        cfg.colorMarco    = btnColorMarco.getBackground();
        cfg.anchoMarco    = num(spnAnchoMarco);
        cfg.imprimirMarco = chkMarco.isSelected();

        cfg.tituloReverso      = tfTituloReverso.getText();
        cfg.textoReverso       = taTextoReverso.getText();
        cfg.estiloTextoReverso = peTextoRev.getEstilo();

        cfg.tamañoPagina = cmbTamañoPagina.getSelectedIndex(); // 0=Carta, 1=Oficio

        cfg.fechaJuego   = tfFecha.getText();
        cfg.fechaDestino = cmbFechaDestino.getSelectedIndex();
        cfg.estiloFecha  = peFecha.getEstilo();

        cfg.valor        = tfValor.getText();
        cfg.valorDestino = cmbValorDestino.getSelectedIndex();
        cfg.estiloValor  = peValor.getEstilo();

        cfg.caducidad        = tfCaducidad.getText();
        cfg.caducidadDestino = cmbCaducidadDestino.getSelectedIndex();

        String ruta = tfMarcaAgua.getText().trim();
        cfg.rutaMarcaAgua     = ruta.isEmpty() ? null : ruta;
        cfg.marcaAguaDestino  = cmbMarcaAguaDest.getSelectedIndex();
        cfg.marcaAguaOpacidad = sldOpacidad.getValue() / 100f;

        cfg.generarQR = chkQR.isSelected();
        cfg.estiloQR  = peQR.getEstilo();

        cfg.generarQRGanador    = chkQRGanador.isSelected();
        cfg.mensajePremioDefecto = tfMensajeDefecto.getText();
        cfg.premios             = leerPremios();
        return cfg;
    }

    /** Convierte las filas de la tabla de premios en objetos RangoPremio. */
    private List<RangoPremio> leerPremios() {
        List<RangoPremio> lista = new ArrayList<>();
        for (int i = 0; i < premiosModel.getRowCount(); i++) {
            Object od = premiosModel.getValueAt(i, 0);
            Object oh = premiosModel.getValueAt(i, 1);
            String mensaje = String.valueOf(premiosModel.getValueAt(i, 2)).trim();
            String fecha   = String.valueOf(premiosModel.getValueAt(i, 3)).trim();
            int desde = parseInt(od);
            int hasta = parseInt(oh);
            if (desde <= 0 && hasta <= 0 && mensaje.isEmpty()) continue;   // fila vacía
            if (hasta < desde) { int tmp = desde; desde = hasta; hasta = tmp; }
            lista.add(new RangoPremio(desde, hasta, mensaje, fecha));
        }
        return lista;
    }

    private int parseInt(Object o) {
        if (o == null) return 0;
        String s = o.toString().trim();
        if (s.isEmpty()) return 0;
        return Integer.parseInt(s);   // soporta ceros a la izquierda ("0010" → 10)
    }

    // =====================================================================
    // Selección de imagen y preview
    // =====================================================================

    private void elegirImagen() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes (PNG, JPG, GIF)", "png", "jpg", "jpeg", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            tfMarcaAgua.setText(path);
            actualizarPreview(path);
        }
    }

    private void actualizarPreview(String path) {
        try {
            BufferedImage original = ImageIO.read(new File(path));
            if (original == null) throw new IOException("Formato no reconocido");
            int maxPx = 130;
            double escala = Math.min((double) maxPx / original.getWidth(),
                                     (double) maxPx / original.getHeight());
            int nw = (int) (original.getWidth()  * escala);
            int nh = (int) (original.getHeight() * escala);
            java.awt.Image scaled = original.getScaledInstance(nw, nh, java.awt.Image.SCALE_SMOOTH);
            lblPreviewMarca.setIcon(new ImageIcon(scaled));
            lblPreviewMarca.setText(null);
        } catch (Exception ex) {
            lblPreviewMarca.setIcon(null);
            lblPreviewMarca.setText("Error al cargar");
        }
    }

    // =====================================================================
    // Ayudantes UI
    // =====================================================================

    private JButton botonColor(Color inicial, String tooltip) {
        JButton btn = new JButton("  ");
        btn.setBackground(inicial);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(50, 22));
        btn.setToolTipText(tooltip);
        btn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, tooltip, btn.getBackground());
            if (c != null) btn.setBackground(c);
        });
        return btn;
    }

    private JComboBox<String> comboDest() {
        return new JComboBox<>(new String[]{"Solo Frente", "Solo Reverso", "Ambos"});
    }

    private JPanel panel(String titulo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(tituloBorde(titulo));
        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private void fila(JPanel p, GridBagConstraints c, int row, String lbl, JComponent comp) {
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel(lbl), c);
        c.gridx = 1; c.weightx = 1;
        p.add(comp, c);
    }

    private void filaComp(JPanel p, GridBagConstraints c, int row, JComponent comp) {
        c.gridy = row; c.gridx = 1; c.weightx = 1;
        p.add(comp, c);
    }

    private TitledBorder tituloBorde(String t) {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), t);
    }

    private float num(JSpinner sp) {
        return ((Number) sp.getValue()).floatValue();
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    @FunctionalInterface
    interface Tarea { void ejecutar() throws Exception; }

    private void ejecutar(Tarea tarea) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(() -> {
            try { tarea.ejecutar(); }
            catch (Exception ex) { mostrarError(ex); }
            finally { SwingUtilities.invokeLater(() -> setCursor(Cursor.getDefaultCursor())); }
        }).start();
    }

    private void mostrarExito(String msg) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, msg, "Listo", JOptionPane.INFORMATION_MESSAGE));
    }

    private void mostrarError(Exception ex) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this,
                        "Error al generar PDF:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
    }

    private String sinExtension(String p) {
        return p.toLowerCase().endsWith(".pdf") ? p.substring(0, p.length() - 4) : p;
    }

    private File asegurarPDF(File f) {
        return f.getName().toLowerCase().endsWith(".pdf")
                ? f : new File(f.getAbsolutePath() + ".pdf");
    }

    private String nombreSugerido(int modo) {
        switch (modo) {
            case ConfigReporte.MODO_FRENTE:     return "Bingo_Frentes.pdf";
            case ConfigReporte.MODO_REVERSO:    return "Bingo_Reversos.pdf";
            case ConfigReporte.MODO_DOBLE_CARA: return "Bingo_DobleCara.pdf";
            default: return "Bingo.pdf";
        }
    }

    // =====================================================================
    // Main
    // =====================================================================

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Modo interno de KernelClic: generador de claves a partir de un ID de equipo.
        boolean keygen = false;
        for (String a : args) if ("--keygen".equalsIgnoreCase(a)) keygen = true;
        if (keygen) {
            SwingUtilities.invokeLater(ReporteUniversal::mostrarGeneradorClave);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // Puerta de seguridad: el aplicativo solo corre en equipos activados.
            if (!asegurarLicencia()) { System.exit(0); return; }
            try { new ReporteUniversal().setVisible(true); }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // =====================================================================
    // Seguridad: activación atada al equipo (node-lock)
    // =====================================================================

    /**
     * Verifica que el equipo tenga una licencia válida. Si no, muestra el
     * diálogo de activación con el ID de equipo y un campo para la clave.
     * Devuelve {@code true} si el equipo queda (o ya estaba) activado.
     */
    private static boolean asegurarLicencia() {
        if (Licencia.estaActivado()) return true;

        while (true) {
            String id = Licencia.idEquipo();

            JTextField tfClave = new JTextField(24);
            JTextField tfId     = new JTextField(id);
            tfId.setEditable(false);
            tfId.setBorder(BorderFactory.createEmptyBorder());
            tfId.setOpaque(false);
            tfId.setFont(tfId.getFont().deriveFont(Font.BOLD));

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 6, 4, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill   = GridBagConstraints.HORIZONTAL;

            c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
            panel.add(new JLabel("<html>Este equipo no está activado.<br>"
                    + "Entregue el <b>ID de equipo</b> a KernelClic para obtener su clave.</html>"), c);
            c.gridwidth = 1;
            c.gridx = 0; c.gridy = 1; panel.add(new JLabel("ID de equipo:"), c);
            c.gridx = 1; c.gridy = 1; panel.add(tfId, c);
            c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Clave de activación:"), c);
            c.gridx = 1; c.gridy = 2; panel.add(tfClave, c);

            int opcion = JOptionPane.showConfirmDialog(null, panel,
                    "Activación del aplicativo — KernelClic",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) return false; // canceló → no se ejecuta

            if (Licencia.activar(tfClave.getText())) {
                JOptionPane.showMessageDialog(null,
                        "Equipo activado correctamente.", "Listo",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null,
                    "Clave incorrecta para este equipo. Verifique e intente de nuevo.",
                    "Activación fallida", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Herramienta interna de KernelClic (lanzar con argumento {@code --keygen}):
     * recibe el ID de equipo del cliente y devuelve la clave de activación.
     */
    private static void mostrarGeneradorClave() {
        JTextField tfId    = new JTextField(24);
        JTextField tfClave = new JTextField(28);
        tfClave.setEditable(false);
        JButton btnGen = new JButton("Generar clave");
        btnGen.addActionListener(e ->
                tfClave.setText(Licencia.generarClaveDesdeId(tfId.getText())));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("ID de equipo del cliente:"), c);
        c.gridx = 1; c.gridy = 0; panel.add(tfId, c);
        c.gridx = 1; c.gridy = 1; panel.add(btnGen, c);
        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Clave de activación:"), c);
        c.gridx = 1; c.gridy = 2; panel.add(tfClave, c);

        JFrame f = new JFrame("Generador de Claves — KernelClic (uso interno)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(panel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
