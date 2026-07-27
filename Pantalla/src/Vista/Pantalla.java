/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Controlador.AccesoAleatorio;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import Controlador.Conector;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Modelo.Ganador;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

/**
 *
 * @author oracle
 */
public final class Pantalla extends javax.swing.JFrame {

    /**
     * Creates new form Pantall
     */
    private static final int MAXBINGO = 75;
    private static final int MAXWTABLAS = 10;

    private Color cTitulo;
    private Color cNumero;
    private Color cNumeroSeleccionado;
    private Color cFondo;

    private Vector<Ganador> ganadores;
    private Vector vBingo;
    private Vector vWinTablas;
    private Vector vImagenes;
    private java.util.List<String> matrices;   // patrones cargados de matriz.txt
    private java.util.List<String> nombresFiguras;              // nombre interno de cada figura
    private java.util.List<String> nombresMostrar;              // traduccion/nombre a mostrar
    private java.util.List<javax.swing.JCheckBox> figChecks;    // seleccion por figura
    private javax.swing.JPanel figLista;                        // contenedor de las casillas
    private javax.swing.JPanel tiraFiguras;                     // tira horizontal de figuras en juego
    private javax.swing.JButton bRecargar, bEditar;            // controles de edicion (ocultos)
    private javax.swing.JPanel figBarraEdicion;                // fila de botones de edicion

    private int index;
    private Conector con;

    // ==== Modo "partida programada" (config.ker) ====
    // Cuando hay partida en /Bingo/db/config.ker, Entrada carga estos valores y activa
    // modoProgramado. En ese modo, la deteccion de ganador usa las sobrecargas
    // del Conector que fuerzan a ganar los cartones pre-fijados (codTablaXY).
    // Sin partida guardada, modoProgramado=false y el comportamiento es el de siempre.
    private boolean modoProgramado = false;
    private int iteracion = -1;
    private int Contador = 0;
    private String msgJuego = "-1";
    private String codTabla = "-1", codTabla1 = "-1", codTabla2 = "-1", codTabla3 = "-1";
    private String pT01 = "", pT02 = "", pT03 = "";
    // Nro de bola configurado por figura (guardado por fidelidad; el amaño real
    // lo aplica la sobrecarga del Conector). Indices segun SetConfigLetra.
    private int nroBalP, nroBalU, nroBalT, nroBalL, nroBalX, nroBalZ, nroBalO,
            nroBalN, nroBalC, nroBalH, nroBalI;
    // Cartones pre-fijados por figura: codTabla<opc>0 / codTabla<opc>1.
    private String codTabla10 = "-1", codTabla11 = "-1"; // 1  Pleno/P
    private String codTabla20 = "-1", codTabla21 = "-1"; // 2  U Grande
    private String codTabla30 = "-1", codTabla31 = "-1"; // 3  Letra T
    private String codTabla40 = "-1", codTabla41 = "-1"; // 4  Letra L
    private String codTabla50 = "-1", codTabla51 = "-1"; // 5  Letra X
    private String codTabla60 = "-1", codTabla61 = "-1"; // 6  Letra Z
    private String codTabla70 = "-1", codTabla71 = "-1"; // 7  Letra O
    private String codTabla80 = "-1", codTabla81 = "-1"; // 8  Letra N
    private String codTabla90 = "-1", codTabla91 = "-1"; // 9  Letra C
    private String codTabla100 = "-1", codTabla101 = "-1"; // 10 Letra H
    private String codTabla110 = "-1", codTabla111 = "-1"; // 11 Letra I
    private String codTabla120 = "-1", codTabla121 = "-1"; // 12 Letra S
    private String codTabla130 = "-1", codTabla131 = "-1"; // 13 Letra E
    private String codTabla140 = "-1", codTabla141 = "-1"; // 14 L invertida
    private String codTabla150 = "-1", codTabla151 = "-1"; // 15 Cuadrado
    private String codTabla160 = "-1", codTabla161 = "-1"; // 16 Casita

    private WinPantalla win;
    private updPassword winUpdPassword;

    InputStream imgStream;

    public static void main(String args[]) throws IOException {
        /* Create and display the form */

        if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFileDB() + "tablas.db")) || !AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFiledb()))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }
        
        AccesoAleatorio.getLicencia();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Pantalla().setVisible(true);
                
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        

    }

    public Pantalla() throws IOException {
        index = 1;
        
        winUpdPassword = new updPassword();
        winUpdPassword.setVisible(false);
    
        
        vImagenes = new Vector(14);
        ganadores = new Vector<Ganador>();
        vWinTablas = new Vector(MAXWTABLAS);
        vBingo = new Vector(MAXBINGO);
        vBingo.addElement("-1");
        matrices = new java.util.ArrayList<>();

        cTitulo = Color.RED;
        cNumero = Color.darkGray;
        cFondo = Color.BLACK;
        cNumeroSeleccionado = Color.YELLOW;

        initComponents();
        this.setLocationRelativeTo(null);

        this.setColorFondo(cFondo);
        this.setColorNumeros(cNumero);
        this.setColorTitulo(cTitulo);

        con = new Conector();
        con.connectConsulta();
        // Cargar las figuras configurables desde /Bingo/db/matriz.txt (si existe).
        try {
            matrices = con.obtenerImagenesPredisenadas();
            nombresFiguras = con.getNombresFiguras();
            nombresMostrar = con.getNombresMostrar();
        } catch (Exception ex) {
            matrices = new java.util.ArrayList<>();
            nombresFiguras = new java.util.ArrayList<>();
            nombresMostrar = new java.util.ArrayList<>();
        }
        if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFileDB() + "tablas.db")) || !AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFiledb()))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }
        this.mostrarPanel.setVisible(false);
        
        ImageIcon iic=new ImageIcon(getClass().getResource("/Imagenes/Limpio.bmp"));
        imagen1.setIcon(iic);
        imagen2.setIcon(iic);
        imagen3.setIcon(iic);
        imagen4.setIcon(iic);

        construirTiraFiguras();
        construirTabFiguras();

        // Gesto oculto: Ctrl+Shift+DobleClic en el cuadro del titulo "BINGO"
        // muestra/oculta los controles de edicion/recarga de figuras.
        titulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && e.isControlDown() && e.isShiftDown()
                        && figBarraEdicion != null) {
                    figBarraEdicion.setVisible(!figBarraEdicion.isVisible());
                    figBarraEdicion.revalidate();
                    figBarraEdicion.repaint();
                }
            }
        });

    }

    /**
     * Construye la pestaña "Figuras" con una casilla por cada figura cargada de
     * matriz.txt (con su nombre y una vista previa del patron). Solo juegan y se
     * muestran las figuras marcadas por el usuario.
     */
    private void construirTabFiguras() {
        figChecks = new java.util.ArrayList<>();
        figLista = new javax.swing.JPanel();
        figLista.setLayout(new javax.swing.BoxLayout(figLista, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.JPanel barra = new javax.swing.JPanel();
        barra.setLayout(new javax.swing.BoxLayout(barra, javax.swing.BoxLayout.Y_AXIS));
        javax.swing.JButton bTodas = new javax.swing.JButton("Todas");
        javax.swing.JButton bNinguna = new javax.swing.JButton("Ninguna");
        bRecargar = new javax.swing.JButton("Recargar");
        bEditar = new javax.swing.JButton("Editar figuras…");
        bTodas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                for (javax.swing.JCheckBox c : figChecks) c.setSelected(true);
                mostrarImagenes();
            }
        });
        bNinguna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                for (javax.swing.JCheckBox c : figChecks) c.setSelected(false);
                mostrarImagenes();
            }
        });
        bRecargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { recargarFiguras(); }
        });
        bEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { abrirEditorFiguras(); }
        });
        javax.swing.JPanel filaA = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));
        filaA.add(new javax.swing.JLabel("Figuras a jugar:"));
        filaA.add(bTodas);
        filaA.add(bNinguna);
        figBarraEdicion = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));
        figBarraEdicion.add(bRecargar);
        figBarraEdicion.add(bEditar);
        figBarraEdicion.setVisible(false);   // oculto hasta el gesto Ctrl+Shift+DobleClic en "BINGO"
        barra.add(filaA);
        barra.add(figBarraEdicion);

        poblarFigLista();

        javax.swing.JPanel cont = new javax.swing.JPanel(new java.awt.BorderLayout());
        cont.add(barra, java.awt.BorderLayout.NORTH);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(figLista);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        cont.add(scroll, java.awt.BorderLayout.CENTER);

        // Se elimina el tab "Juegos" (14 figuras integradas): ahora TODO se juega
        // desde este tab, con las figuras configurables de matriz.txt.
        try { panelConfiguracion.remove(jPanel4); } catch (Exception ignore) {}
        panelConfiguracion.insertTab("Figuras", null, cont, "Figuras a jugar", 1);
        panelConfiguracion.setSelectedIndex(1);
    }

    /** (Re)construye la lista de casillas del tab Figuras desde matrices/nombresFiguras. */
    private void poblarFigLista() {
        if (figLista == null) return;
        figChecks.clear();
        figLista.removeAll();
        int n = (matrices == null) ? 0 : matrices.size();
        for (int i = 0; i < n; i++) {
            String nom = (nombresFiguras != null && i < nombresFiguras.size())
                    ? nombresFiguras.get(i) : ("Figura " + (i + 1));
            javax.swing.JCheckBox chk = new javax.swing.JCheckBox(nom);
            chk.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) { mostrarImagenes(); }
            });
            javax.swing.JLabel prev = new javax.swing.JLabel(iconoDePatron(matrices.get(i), 46));
            javax.swing.JPanel fila = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 2));
            fila.add(prev);
            fila.add(chk);
            figChecks.add(chk);
            figLista.add(fila);
        }
        figLista.revalidate();
        figLista.repaint();
    }

    /** Vuelve a leer matriz.txt y reconstruye la lista (boton "Recargar"). */
    private void recargarFiguras() {
        try {
            matrices = con.obtenerImagenesPredisenadas();
            nombresFiguras = con.getNombresFiguras();
            nombresMostrar = con.getNombresMostrar();
        } catch (Exception ex) {
            matrices = new java.util.ArrayList<>();
            nombresFiguras = new java.util.ArrayList<>();
            nombresMostrar = new java.util.ArrayList<>();
        }
        poblarFigLista();
        mostrarImagenes();
    }

    /**
     * Editor de figuras: crear/editar/eliminar patrones 5x5 y guardarlos en
     * matriz.txt (boton "Editar figuras…"). Al guardar, recarga la lista.
     */
    private void abrirEditorFiguras() {
        final javax.swing.JDialog d = new javax.swing.JDialog(this, "Editor de figuras", true);
        d.setLayout(new java.awt.BorderLayout(8, 8));

        final javax.swing.DefaultListModel<String> modelo = new javax.swing.DefaultListModel<>();
        for (int i = 0; i < nombresFiguras.size(); i++) modelo.addElement(nombresFiguras.get(i));
        final javax.swing.JList<String> lst = new javax.swing.JList<>(modelo);
        lst.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        javax.swing.JScrollPane lsc = new javax.swing.JScrollPane(lst);
        lsc.setPreferredSize(new java.awt.Dimension(170, 280));

        final javax.swing.JToggleButton[][] tg = new javax.swing.JToggleButton[5][5];
        javax.swing.JPanel grid = new javax.swing.JPanel(new java.awt.GridLayout(5, 5, 2, 2));
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                javax.swing.JToggleButton b = new javax.swing.JToggleButton();
                b.setPreferredSize(new java.awt.Dimension(40, 40));
                tg[r][c] = b;
                grid.add(b);
            }
        }
        final javax.swing.JTextField tfNom = new javax.swing.JTextField(14);
        final javax.swing.JTextField tfMostrar = new javax.swing.JTextField(14);

        lst.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                int idx = lst.getSelectedIndex();
                if (idx < 0 || idx >= matrices.size()) return;
                tfNom.setText(nombresFiguras.get(idx));
                tfMostrar.setText((nombresMostrar != null && idx < nombresMostrar.size())
                        ? nombresMostrar.get(idx) : "");
                String[] filas = matrices.get(idx).split("\n");
                for (int r = 0; r < 5; r++) {
                    String f = (r < filas.length) ? filas[r] : "00000";
                    for (int c = 0; c < 5; c++)
                        tg[r][c].setSelected(c < f.length() && (f.charAt(c) == 'X' || f.charAt(c) == 'x'));
                }
            }
        });

        javax.swing.JButton bNueva = new javax.swing.JButton("Nueva");
        javax.swing.JButton bGuardar = new javax.swing.JButton("Guardar");
        javax.swing.JButton bEliminar = new javax.swing.JButton("Eliminar");
        javax.swing.JButton bCerrar = new javax.swing.JButton("Cerrar");

        bNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                lst.clearSelection();
                tfNom.setText("");
                tfMostrar.setText("");
                for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++) tg[r][c].setSelected(false);
            }
        });
        bGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                for (int r = 0; r < 5; r++) {
                    for (int c = 0; c < 5; c++) sb.append(tg[r][c].isSelected() ? 'X' : '0');
                    sb.append('\n');
                }
                String m = sb.toString();
                String nom = tfNom.getText().trim();
                if (nom.isEmpty()) nom = "Figura " + (matrices.size() + 1);
                String tra = tfMostrar.getText().trim();
                int idx = lst.getSelectedIndex();
                if (idx >= 0 && idx < matrices.size()) {
                    matrices.set(idx, m);
                    nombresFiguras.set(idx, nom);
                    if (idx < nombresMostrar.size()) nombresMostrar.set(idx, tra);
                    else nombresMostrar.add(tra);
                } else {
                    matrices.add(m);
                    nombresFiguras.add(nom);
                    nombresMostrar.add(tra);
                }
                try {
                    con.guardarFiguras(nombresFiguras, nombresMostrar, matrices);
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(d, "No se pudo guardar:\n" + ex.getMessage(),
                            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modelo.clear();
                for (String x : nombresFiguras) modelo.addElement(x);
                poblarFigLista();
                mostrarImagenes();
            }
        });
        bEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int idx = lst.getSelectedIndex();
                if (idx < 0 || idx >= matrices.size()) return;
                matrices.remove(idx);
                nombresFiguras.remove(idx);
                if (idx < nombresMostrar.size()) nombresMostrar.remove(idx);
                try {
                    con.guardarFiguras(nombresFiguras, nombresMostrar, matrices);
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(d, "No se pudo guardar:\n" + ex.getMessage(),
                            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modelo.remove(idx);
                tfNom.setText("");
                tfMostrar.setText("");
                for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++) tg[r][c].setSelected(false);
                poblarFigLista();
                mostrarImagenes();
            }
        });
        bCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { d.dispose(); }
        });

        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        top.add(new javax.swing.JLabel("Nombre:"));
        top.add(tfNom);
        top.add(new javax.swing.JLabel("Mostrar (país):"));
        top.add(tfMostrar);
        javax.swing.JPanel bot = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        bot.add(bNueva);
        bot.add(bGuardar);
        bot.add(bEliminar);
        bot.add(bCerrar);
        javax.swing.JPanel centro = new javax.swing.JPanel(new java.awt.BorderLayout(6, 6));
        centro.add(top, java.awt.BorderLayout.NORTH);
        javax.swing.JPanel gridWrap = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        gridWrap.add(grid);
        centro.add(gridWrap, java.awt.BorderLayout.CENTER);
        centro.add(bot, java.awt.BorderLayout.SOUTH);

        javax.swing.JPanel izq = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
        izq.add(new javax.swing.JLabel("Figuras:"), java.awt.BorderLayout.NORTH);
        izq.add(lsc, java.awt.BorderLayout.CENTER);

        d.add(izq, java.awt.BorderLayout.WEST);
        d.add(centro, java.awt.BorderLayout.CENTER);
        d.getRootPane().setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // ==== API de carga de la partida (invocada por Entrada en modo programado) ====

    /** Activa/desactiva el modo partida programada. */
    public void setModoProgramado(boolean b) {
        this.modoProgramado = b;
    }

    public boolean isModoProgramado() {
        return this.modoProgramado;
    }

    /** Registro 1: parametros generales del juego. */
    public void setTablas(String t1, String t2, String t3) {
        this.pT01 = t1;
        this.pT02 = t2;
        this.pT03 = t3;
    }

    public void setIteracion(int i) {
        this.iteracion = i;
    }

    public void setcodTabla(String ct) {
        this.codTabla = ct;
    }

    public void setcodTabla1(String ct) {
        this.codTabla1 = ct;
    }

    public void setcodTabla2(String ct) {
        this.codTabla2 = ct;
    }

    public void setcodTabla3(String ct) {
        this.codTabla3 = ct;
    }

    public void setmsgJuego(String mj) {
        this.msgJuego = mj;
    }

    /**
     * Configura una figura de la partida programada.
     *
     * @param opc    indice de figura (1=Pleno, 2=U Grande, 3=T, 4=L, 5=X, ...)
     * @param nroBal numero de bola configurado (guardado por fidelidad)
     * @param tabla1 primer carton pre-fijado ganador
     * @param tabla2 segundo carton pre-fijado ganador
     */
    public void SetConfigLetra(int opc, int nroBal, String tabla1, String tabla2) {
        switch (opc) {
            case 1:  nroBalP = nroBal; codTabla10 = tabla1; codTabla11 = tabla2; break;
            case 2:  nroBalU = nroBal; codTabla20 = tabla1; codTabla21 = tabla2; break;
            case 3:  nroBalT = nroBal; codTabla30 = tabla1; codTabla31 = tabla2; break;
            case 4:  nroBalL = nroBal; codTabla40 = tabla1; codTabla41 = tabla2; break;
            case 5:  nroBalX = nroBal; codTabla50 = tabla1; codTabla51 = tabla2; break;
            case 6:  nroBalZ = nroBal; codTabla60 = tabla1; codTabla61 = tabla2; break;
            case 7:  nroBalO = nroBal; codTabla70 = tabla1; codTabla71 = tabla2; break;
            case 8:  nroBalN = nroBal; codTabla80 = tabla1; codTabla81 = tabla2; break;
            case 9:  nroBalC = nroBal; codTabla90 = tabla1; codTabla91 = tabla2; break;
            case 10: nroBalH = nroBal; codTabla100 = tabla1; codTabla101 = tabla2; break;
            case 11: nroBalI = nroBal; codTabla110 = tabla1; codTabla111 = tabla2; break;
            case 12: codTabla120 = tabla1; codTabla121 = tabla2; break;
            case 13: codTabla130 = tabla1; codTabla131 = tabla2; break;
            case 14: codTabla140 = tabla1; codTabla141 = tabla2; break;
            case 15: codTabla150 = tabla1; codTabla151 = tabla2; break;
            case 16: codTabla160 = tabla1; codTabla161 = tabla2; break;
            default: break;
        }
    }

    private void initComponents() {

        Fondo = new javax.swing.JPanel();
        T1 = new javax.swing.JLabel();
        T2 = new javax.swing.JLabel();
        T3 = new javax.swing.JLabel();
        T4 = new javax.swing.JLabel();
        T5 = new javax.swing.JLabel();
        b1 = new javax.swing.JLabel();
        i1 = new javax.swing.JLabel();
        n1 = new javax.swing.JLabel();
        g1 = new javax.swing.JLabel();
        o1 = new javax.swing.JLabel();
        b2 = new javax.swing.JLabel();
        i2 = new javax.swing.JLabel();
        n2 = new javax.swing.JLabel();
        g2 = new javax.swing.JLabel();
        o2 = new javax.swing.JLabel();
        b3 = new javax.swing.JLabel();
        i3 = new javax.swing.JLabel();
        n3 = new javax.swing.JLabel();
        g3 = new javax.swing.JLabel();
        o3 = new javax.swing.JLabel();
        b4 = new javax.swing.JLabel();
        i4 = new javax.swing.JLabel();
        n4 = new javax.swing.JLabel();
        g4 = new javax.swing.JLabel();
        o4 = new javax.swing.JLabel();
        b5 = new javax.swing.JLabel();
        i5 = new javax.swing.JLabel();
        n5 = new javax.swing.JLabel();
        g5 = new javax.swing.JLabel();
        o5 = new javax.swing.JLabel();
        b6 = new javax.swing.JLabel();
        i6 = new javax.swing.JLabel();
        n6 = new javax.swing.JLabel();
        g6 = new javax.swing.JLabel();
        o6 = new javax.swing.JLabel();
        b7 = new javax.swing.JLabel();
        i7 = new javax.swing.JLabel();
        n7 = new javax.swing.JLabel();
        g7 = new javax.swing.JLabel();
        o7 = new javax.swing.JLabel();
        b8 = new javax.swing.JLabel();
        i8 = new javax.swing.JLabel();
        n8 = new javax.swing.JLabel();
        g8 = new javax.swing.JLabel();
        o8 = new javax.swing.JLabel();
        b9 = new javax.swing.JLabel();
        i9 = new javax.swing.JLabel();
        n9 = new javax.swing.JLabel();
        g9 = new javax.swing.JLabel();
        o9 = new javax.swing.JLabel();
        b10 = new javax.swing.JLabel();
        i10 = new javax.swing.JLabel();
        n10 = new javax.swing.JLabel();
        g10 = new javax.swing.JLabel();
        o10 = new javax.swing.JLabel();
        b11 = new javax.swing.JLabel();
        i11 = new javax.swing.JLabel();
        n11 = new javax.swing.JLabel();
        g11 = new javax.swing.JLabel();
        o11 = new javax.swing.JLabel();
        b12 = new javax.swing.JLabel();
        i12 = new javax.swing.JLabel();
        n12 = new javax.swing.JLabel();
        g12 = new javax.swing.JLabel();
        o12 = new javax.swing.JLabel();
        b13 = new javax.swing.JLabel();
        i13 = new javax.swing.JLabel();
        n13 = new javax.swing.JLabel();
        g13 = new javax.swing.JLabel();
        o13 = new javax.swing.JLabel();
        b14 = new javax.swing.JLabel();
        i14 = new javax.swing.JLabel();
        n14 = new javax.swing.JLabel();
        g14 = new javax.swing.JLabel();
        o14 = new javax.swing.JLabel();
        b15 = new javax.swing.JLabel();
        i15 = new javax.swing.JLabel();
        n15 = new javax.swing.JLabel();
        g15 = new javax.swing.JLabel();
        o15 = new javax.swing.JLabel();
        panelConfiguracion = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        titulo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        colorFondo = new javax.swing.JButton();
        colorTitulo = new javax.swing.JButton();
        colorNumero = new javax.swing.JButton();
        colorNumeroSeleccionado = new javax.swing.JButton();
        cambiaTitulo = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        juegoPleno = new javax.swing.JCheckBox();
        juegoLetraL = new javax.swing.JCheckBox();
        juegoCruzPequeña = new javax.swing.JCheckBox();
        juegoCruzGrande = new javax.swing.JCheckBox();
        juegoLetraX = new javax.swing.JCheckBox();
        juegoCuatroEsquinas = new javax.swing.JCheckBox();
        juegoMachetazoIzquierdo = new javax.swing.JCheckBox();
        juegoLetraT = new javax.swing.JCheckBox();
        juegoVerticalCentral = new javax.swing.JCheckBox();
        juegoMachetazoDerecho = new javax.swing.JCheckBox();
        juegoHorizontalCentral = new javax.swing.JCheckBox();
        juegoPuntaFlecha = new javax.swing.JCheckBox();
        juegoLetraUPequeña = new javax.swing.JCheckBox();
        juegoArchivo = new javax.swing.JCheckBox();
        juegoLetraUGrande = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        segundoMensaje = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        colorFondoGanadores = new javax.swing.JButton();
        colorTituloGanadores = new javax.swing.JButton();
        colorNumeroTablas = new javax.swing.JButton();
        btn_SalirGenerador = new javax.swing.JButton();
        btn_updPassword = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        reiniciarBingo = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        mostrarPanel = new javax.swing.JButton();
        imagen1 = new javax.swing.JLabel();
        imagen2 = new javax.swing.JLabel();
        imagen3 = new javax.swing.JLabel();
        imagen4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Fondo.setBackground(new java.awt.Color(0, 0, 0));
        Fondo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        T1.setFont(new java.awt.Font("URW Palladio L", 1, 74)); // NOI18N
        T1.setForeground(new java.awt.Color(255, 255, 255));
        T1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        T1.setText("B");
        T1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        T2.setFont(new java.awt.Font("URW Palladio L", 1, 74)); // NOI18N
        T2.setForeground(new java.awt.Color(255, 255, 255));
        T2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        T2.setText("I");
        T2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        T3.setFont(new java.awt.Font("URW Palladio L", 1, 74)); // NOI18N
        T3.setForeground(new java.awt.Color(255, 255, 255));
        T3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        T3.setText("N");
        T3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        T4.setFont(new java.awt.Font("URW Palladio L", 1, 74)); // NOI18N
        T4.setForeground(new java.awt.Color(255, 255, 255));
        T4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        T4.setText("G");
        T4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        T5.setFont(new java.awt.Font("URW Palladio L", 1, 74)); // NOI18N
        T5.setForeground(new java.awt.Color(255, 255, 255));
        T5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        T5.setText("O");
        T5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        b1.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b1.setForeground(new java.awt.Color(255, 255, 255));
        b1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b1.setText("1");
        b1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b1MouseClicked(evt);
            }
        });

        i1.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i1.setForeground(new java.awt.Color(255, 255, 255));
        i1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i1.setText("16");
        i1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i1MouseClicked(evt);
            }
        });

        n1.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n1.setForeground(new java.awt.Color(255, 255, 255));
        n1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n1.setText("31");
        n1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n1MouseClicked(evt);
            }
        });

        g1.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g1.setForeground(new java.awt.Color(255, 255, 255));
        g1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g1.setText("46");
        g1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g1MouseClicked(evt);
            }
        });

        o1.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o1.setForeground(new java.awt.Color(255, 255, 255));
        o1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o1.setText("61");
        o1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o1MouseClicked(evt);
            }
        });

        b2.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b2.setForeground(new java.awt.Color(255, 255, 255));
        b2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b2.setText("2");
        b2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b2MouseClicked(evt);
            }
        });

        i2.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i2.setForeground(new java.awt.Color(255, 255, 255));
        i2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i2.setText("17");
        i2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i2MouseClicked(evt);
            }
        });

        n2.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n2.setForeground(new java.awt.Color(255, 255, 255));
        n2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n2.setText("32");
        n2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n2MouseClicked(evt);
            }
        });

        g2.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g2.setForeground(new java.awt.Color(255, 255, 255));
        g2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g2.setText("47");
        g2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g2MouseClicked(evt);
            }
        });

        o2.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o2.setForeground(new java.awt.Color(255, 255, 255));
        o2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o2.setText("62");
        o2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o2MouseClicked(evt);
            }
        });

        b3.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b3.setForeground(new java.awt.Color(255, 255, 255));
        b3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b3.setText("3");
        b3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b3MouseClicked(evt);
            }
        });

        i3.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i3.setForeground(new java.awt.Color(255, 255, 255));
        i3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i3.setText("18");
        i3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i3MouseClicked(evt);
            }
        });

        n3.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n3.setForeground(new java.awt.Color(255, 255, 255));
        n3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n3.setText("33");
        n3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n3MouseClicked(evt);
            }
        });

        g3.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g3.setForeground(new java.awt.Color(255, 255, 255));
        g3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g3.setText("48");
        g3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g3MouseClicked(evt);
            }
        });

        o3.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o3.setForeground(new java.awt.Color(255, 255, 255));
        o3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o3.setText("63");
        o3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o3MouseClicked(evt);
            }
        });

        b4.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b4.setForeground(new java.awt.Color(255, 255, 255));
        b4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b4.setText("4");
        b4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b4MouseClicked(evt);
            }
        });

        i4.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i4.setForeground(new java.awt.Color(255, 255, 255));
        i4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i4.setText("19");
        i4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i4MouseClicked(evt);
            }
        });

        n4.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n4.setForeground(new java.awt.Color(255, 255, 255));
        n4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n4.setText("34");
        n4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n4MouseClicked(evt);
            }
        });

        g4.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g4.setForeground(new java.awt.Color(255, 255, 255));
        g4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g4.setText("49");
        g4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g4MouseClicked(evt);
            }
        });

        o4.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o4.setForeground(new java.awt.Color(255, 255, 255));
        o4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o4.setText("64");
        o4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                o4MouseMoved(evt);
            }
        });
        o4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o4MouseClicked(evt);
            }
        });

        b5.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b5.setForeground(new java.awt.Color(255, 255, 255));
        b5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b5.setText("5");
        b5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b5MouseClicked(evt);
            }
        });

        i5.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i5.setForeground(new java.awt.Color(255, 255, 255));
        i5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i5.setText("20");
        i5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i5MouseClicked(evt);
            }
        });

        n5.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n5.setForeground(new java.awt.Color(255, 255, 255));
        n5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n5.setText("35");
        n5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n5MouseClicked(evt);
            }
        });

        g5.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g5.setForeground(new java.awt.Color(255, 255, 255));
        g5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g5.setText("50");
        g5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g5MouseClicked(evt);
            }
        });

        o5.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o5.setForeground(new java.awt.Color(255, 255, 255));
        o5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o5.setText("65");
        o5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o5MouseClicked(evt);
            }
        });

        b6.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b6.setForeground(new java.awt.Color(255, 255, 255));
        b6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b6.setText("6");
        b6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b6MouseClicked(evt);
            }
        });

        i6.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i6.setForeground(new java.awt.Color(255, 255, 255));
        i6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i6.setText("21");
        i6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i6MouseClicked(evt);
            }
        });

        n6.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n6.setForeground(new java.awt.Color(255, 255, 255));
        n6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n6.setText("36");
        n6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n6MouseClicked(evt);
            }
        });

        g6.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g6.setForeground(new java.awt.Color(255, 255, 255));
        g6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g6.setText("51");
        g6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g6MouseClicked(evt);
            }
        });

        o6.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o6.setForeground(new java.awt.Color(255, 255, 255));
        o6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o6.setText("66");
        o6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o6MouseClicked(evt);
            }
        });

        b7.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b7.setForeground(new java.awt.Color(255, 255, 255));
        b7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b7.setText("7");
        b7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b7MouseClicked(evt);
            }
        });

        i7.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i7.setForeground(new java.awt.Color(255, 255, 255));
        i7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i7.setText("22");
        i7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i7MouseClicked(evt);
            }
        });

        n7.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n7.setForeground(new java.awt.Color(255, 255, 255));
        n7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n7.setText("37");
        n7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n7MouseClicked(evt);
            }
        });

        g7.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g7.setForeground(new java.awt.Color(255, 255, 255));
        g7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g7.setText("52");
        g7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g7MouseClicked(evt);
            }
        });

        o7.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o7.setForeground(new java.awt.Color(255, 255, 255));
        o7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o7.setText("67");
        o7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o7MouseClicked(evt);
            }
        });

        b8.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b8.setForeground(new java.awt.Color(255, 255, 255));
        b8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b8.setText("8");
        b8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b8MouseClicked(evt);
            }
        });

        i8.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i8.setForeground(new java.awt.Color(255, 255, 255));
        i8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i8.setText("23");
        i8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i8MouseClicked(evt);
            }
        });

        n8.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n8.setForeground(new java.awt.Color(255, 255, 255));
        n8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n8.setText("38");
        n8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n8MouseClicked(evt);
            }
        });

        g8.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g8.setForeground(new java.awt.Color(255, 255, 255));
        g8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g8.setText("53");
        g8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g8MouseClicked(evt);
            }
        });

        o8.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o8.setForeground(new java.awt.Color(255, 255, 255));
        o8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o8.setText("68");
        o8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o8MouseClicked(evt);
            }
        });

        b9.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b9.setForeground(new java.awt.Color(255, 255, 255));
        b9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b9.setText("9");
        b9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b9MouseClicked(evt);
            }
        });

        i9.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i9.setForeground(new java.awt.Color(255, 255, 255));
        i9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i9.setText("24");
        i9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i9MouseClicked(evt);
            }
        });

        n9.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n9.setForeground(new java.awt.Color(255, 255, 255));
        n9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n9.setText("39");
        n9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n9MouseClicked(evt);
            }
        });

        g9.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g9.setForeground(new java.awt.Color(255, 255, 255));
        g9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g9.setText("54");
        g9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g9MouseClicked(evt);
            }
        });

        o9.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o9.setForeground(new java.awt.Color(255, 255, 255));
        o9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o9.setText("69");
        o9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o9MouseClicked(evt);
            }
        });

        b10.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b10.setForeground(new java.awt.Color(255, 255, 255));
        b10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b10.setText("10");
        b10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b10MouseClicked(evt);
            }
        });

        i10.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i10.setForeground(new java.awt.Color(255, 255, 255));
        i10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i10.setText("25");
        i10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i10MouseClicked(evt);
            }
        });

        n10.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n10.setForeground(new java.awt.Color(255, 255, 255));
        n10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n10.setText("40");
        n10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n10MouseClicked(evt);
            }
        });

        g10.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g10.setForeground(new java.awt.Color(255, 255, 255));
        g10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g10.setText("55");
        g10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g10MouseClicked(evt);
            }
        });

        o10.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o10.setForeground(new java.awt.Color(255, 255, 255));
        o10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o10.setText("70");
        o10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o10MouseClicked(evt);
            }
        });

        b11.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b11.setForeground(new java.awt.Color(255, 255, 255));
        b11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b11.setText("11");
        b11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b11MouseClicked(evt);
            }
        });

        i11.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i11.setForeground(new java.awt.Color(255, 255, 255));
        i11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i11.setText("26");
        i11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i11MouseClicked(evt);
            }
        });

        n11.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n11.setForeground(new java.awt.Color(255, 255, 255));
        n11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n11.setText("41");
        n11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n11MouseClicked(evt);
            }
        });

        g11.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g11.setForeground(new java.awt.Color(255, 255, 255));
        g11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g11.setText("56");
        g11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g11MouseClicked(evt);
            }
        });

        o11.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o11.setForeground(new java.awt.Color(255, 255, 255));
        o11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o11.setText("71");
        o11.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o11MouseClicked(evt);
            }
        });

        b12.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b12.setForeground(new java.awt.Color(255, 255, 255));
        b12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b12.setText("12");
        b12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b12MouseClicked(evt);
            }
        });

        i12.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i12.setForeground(new java.awt.Color(255, 255, 255));
        i12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i12.setText("27");
        i12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i12MouseClicked(evt);
            }
        });

        n12.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n12.setForeground(new java.awt.Color(255, 255, 255));
        n12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n12.setText("42");
        n12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n12MouseClicked(evt);
            }
        });

        g12.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g12.setForeground(new java.awt.Color(255, 255, 255));
        g12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g12.setText("57");
        g12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g12MouseClicked(evt);
            }
        });

        o12.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o12.setForeground(new java.awt.Color(255, 255, 255));
        o12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o12.setText("72");
        o12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o12MouseClicked(evt);
            }
        });

        b13.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b13.setForeground(new java.awt.Color(255, 255, 255));
        b13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b13.setText("13");
        b13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b13MouseClicked(evt);
            }
        });

        i13.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i13.setForeground(new java.awt.Color(255, 255, 255));
        i13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i13.setText("28");
        i13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i13MouseClicked(evt);
            }
        });

        n13.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n13.setForeground(new java.awt.Color(255, 255, 255));
        n13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n13.setText("43");
        n13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n13MouseClicked(evt);
            }
        });

        g13.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g13.setForeground(new java.awt.Color(255, 255, 255));
        g13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g13.setText("58");
        g13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g13MouseClicked(evt);
            }
        });

        o13.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o13.setForeground(new java.awt.Color(255, 255, 255));
        o13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o13.setText("73");
        o13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o13MouseClicked(evt);
            }
        });

        b14.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b14.setForeground(new java.awt.Color(255, 255, 255));
        b14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b14.setText("14");
        b14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b14MouseClicked(evt);
            }
        });

        i14.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i14.setForeground(new java.awt.Color(255, 255, 255));
        i14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i14.setText("29");
        i14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i14MouseClicked(evt);
            }
        });

        n14.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n14.setForeground(new java.awt.Color(255, 255, 255));
        n14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n14.setText("44");
        n14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n14MouseClicked(evt);
            }
        });

        g14.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g14.setForeground(new java.awt.Color(255, 255, 255));
        g14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g14.setText("59");
        g14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g14MouseClicked(evt);
            }
        });

        o14.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o14.setForeground(new java.awt.Color(255, 255, 255));
        o14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o14.setText("74");
        o14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o14MouseClicked(evt);
            }
        });

        b15.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        b15.setForeground(new java.awt.Color(255, 255, 255));
        b15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        b15.setText("15");
        b15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        b15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                b15MouseClicked(evt);
            }
        });

        i15.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        i15.setForeground(new java.awt.Color(255, 255, 255));
        i15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        i15.setText("30");
        i15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        i15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                i15MouseClicked(evt);
            }
        });

        n15.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        n15.setForeground(new java.awt.Color(255, 255, 255));
        n15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n15.setText("45");
        n15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        n15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                n15MouseClicked(evt);
            }
        });

        g15.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        g15.setForeground(new java.awt.Color(255, 255, 255));
        g15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        g15.setText("60");
        g15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        g15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                g15MouseClicked(evt);
            }
        });

        o15.setFont(new java.awt.Font("SansSerif", 1, 53)); // NOI18N
        o15.setForeground(new java.awt.Color(255, 255, 255));
        o15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        o15.setText("75");
        o15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        o15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                o15MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout FondoLayout = new javax.swing.GroupLayout(Fondo);
        Fondo.setLayout(FondoLayout);
        FondoLayout.setHorizontalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(T1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(T5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(T4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                        .addComponent(T3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(T2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(n1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(i1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(o1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(n2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(o2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(i2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(i3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(i4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(i15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(n4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(n15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(g3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(g15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(o3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(o15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(46, 46, 46))
        );
        FondoLayout.setVerticalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addComponent(T1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(T2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(i15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(T3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(T4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(g15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(T5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(o1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                        .addContainerGap(15, Short.MAX_VALUE))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b8, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b12, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        T1.getAccessibleContext().setAccessibleName("T1");
        T2.getAccessibleContext().setAccessibleName("T2");
        T3.getAccessibleContext().setAccessibleName("T3");

        jLabel1.setText("Titulo:");

        titulo.setText("BINGO");
        titulo.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tituloInputMethodTextChanged(evt);
            }
        });
        titulo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tituloPropertyChange(evt);
            }
        });
        titulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tituloKeyPressed(evt);
            }
        });

        jLabel2.setText("Colores");

        colorFondo.setText("Fondo");
        colorFondo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFondoActionPerformed(evt);
            }
        });

        colorTitulo.setText("Titulo");
        colorTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorTituloActionPerformed(evt);
            }
        });

        colorNumero.setText("Numeros");
        colorNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorNumeroActionPerformed(evt);
            }
        });

        colorNumeroSeleccionado.setText("Numero Seleccionado");
        colorNumeroSeleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorNumeroSeleccionadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorNumero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(colorNumeroSeleccionado, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorFondo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorNumero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorNumeroSeleccionado)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        cambiaTitulo.setText("Cambiar");
        cambiaTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiaTituloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cambiaTitulo)))
                .addGap(0, 154, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cambiaTitulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelConfiguracion.addTab("Tablero", jPanel3);

        juegoPleno.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoPleno.setText("Pleno");
        juegoPleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoPlenoActionPerformed(evt);
            }
        });

        juegoLetraL.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoLetraL.setText("Letra L");
        juegoLetraL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoLetraLActionPerformed(evt);
            }
        });

        juegoCruzPequeña.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoCruzPequeña.setText("Cruz Pequeña");
        juegoCruzPequeña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoCruzPequeñaActionPerformed(evt);
            }
        });

        juegoCruzGrande.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoCruzGrande.setText("Cruz Grande");
        juegoCruzGrande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoCruzGrandeActionPerformed(evt);
            }
        });

        juegoLetraX.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoLetraX.setText("Letra X");
        juegoLetraX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoLetraXActionPerformed(evt);
            }
        });

        juegoCuatroEsquinas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoCuatroEsquinas.setText("Cuatro Esquinas");
        juegoCuatroEsquinas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoCuatroEsquinasActionPerformed(evt);
            }
        });

        juegoMachetazoIzquierdo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoMachetazoIzquierdo.setText("Machetazo Izquierdo");
        juegoMachetazoIzquierdo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoMachetazoIzquierdoActionPerformed(evt);
            }
        });

        juegoLetraT.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoLetraT.setText("Letra T");
        juegoLetraT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoLetraTActionPerformed(evt);
            }
        });

        juegoVerticalCentral.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoVerticalCentral.setText("Vertical Central");
        juegoVerticalCentral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoVerticalCentralActionPerformed(evt);
            }
        });

        juegoMachetazoDerecho.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoMachetazoDerecho.setText("Machetazo Derecho");
        juegoMachetazoDerecho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoMachetazoDerechoActionPerformed(evt);
            }
        });

        juegoHorizontalCentral.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoHorizontalCentral.setText("Horizontal Central");
        juegoHorizontalCentral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoHorizontalCentralActionPerformed(evt);
            }
        });

        juegoPuntaFlecha.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoPuntaFlecha.setText("Punta Flecha");
        juegoPuntaFlecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoPuntaFlechaActionPerformed(evt);
            }
        });

        juegoLetraUPequeña.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoLetraUPequeña.setText("Letra U Pequeña");
        juegoLetraUPequeña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoLetraUPequeñaActionPerformed(evt);
            }
        });

        juegoLetraUGrande.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoLetraUGrande.setText("Letra U Grande");
        juegoLetraUGrande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoLetraUGrandeActionPerformed(evt);
            }
        });

        juegoArchivo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        juegoArchivo.setText("Figuras de archivo (todas)");
        juegoArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juegoArchivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(juegoPleno)
                            .addComponent(juegoCruzPequeña)
                            .addComponent(juegoCruzGrande)
                            .addComponent(juegoCuatroEsquinas)
                            .addComponent(juegoMachetazoIzquierdo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(juegoHorizontalCentral)
                            .addComponent(juegoVerticalCentral)
                            .addComponent(juegoLetraX)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(juegoLetraT)
                                .addComponent(juegoLetraL)))
                        .addGap(154, 154, 154))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(juegoMachetazoDerecho)
                            .addComponent(juegoPuntaFlecha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(juegoLetraUGrande)
                            .addComponent(juegoLetraUPequeña)
                            .addComponent(juegoArchivo))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoPleno)
                    .addComponent(juegoVerticalCentral))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoCruzPequeña)
                    .addComponent(juegoHorizontalCentral))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoCruzGrande)
                    .addComponent(juegoLetraT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoCuatroEsquinas)
                    .addComponent(juegoLetraL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoMachetazoIzquierdo)
                    .addComponent(juegoLetraX))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoMachetazoDerecho)
                    .addComponent(juegoLetraUPequeña))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(juegoPuntaFlecha)
                    .addComponent(juegoLetraUGrande))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(juegoArchivo)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        panelConfiguracion.addTab("Juegos", jPanel4);

        jLabel4.setText("Segundos en Pantalla");

        segundoMensaje.setText("4");

        jLabel5.setText("Colores");

        colorFondoGanadores.setText("Fondo");
        colorFondoGanadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFondoGanadoresActionPerformed(evt);
            }
        });

        colorTituloGanadores.setText("Titulo");
        colorTituloGanadores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorTituloGanadoresActionPerformed(evt);
            }
        });

        colorNumeroTablas.setText("Numeros de Tabla");
        colorNumeroTablas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorNumeroTablasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(colorFondoGanadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(colorNumeroTablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(colorTituloGanadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorFondoGanadores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorTituloGanadores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorNumeroTablas)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        btn_SalirGenerador.setText("Salir");
        btn_SalirGenerador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SalirGeneradorActionPerformed(evt);
            }
        });

        btn_updPassword.setText("Cambiar Contraseña");
        btn_updPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updPasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(segundoMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_updPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_SalirGenerador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(segundoMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(35, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_updPassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_SalirGenerador)
                        .addGap(53, 53, 53))))
        );

        panelConfiguracion.addTab("Ganadores", jPanel2);

        reiniciarBingo.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        reiniciarBingo.setForeground(new java.awt.Color(255, 0, 0));
        reiniciarBingo.setText("Iniciar");
        reiniciarBingo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reiniciarBingoActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 255));
        jButton1.setText("Mostrar Ganadores");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reiniciarBingo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(43, 43, 43))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(reiniciarBingo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        panelConfiguracion.addTab("Jugar", jPanel6);

        mostrarPanel.setText("x");
        mostrarPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarPanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(imagen1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imagen2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imagen3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imagen4, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(panelConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Fondo, javax.swing.GroupLayout.PREFERRED_SIZE, 1328, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mostrarPanel)
                .addContainerGap(189, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Fondo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mostrarPanel)
                    .addComponent(imagen4, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagen3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagen2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imagen1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        panelConfiguracion.getAccessibleContext().setAccessibleName("Tablero");
        panelConfiguracion.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void o4MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o4MouseMoved
        // TODO add your handling code here:

    }//GEN-LAST:event_o4MouseMoved

    private void b1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b1);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b1MouseClicked

    private void b2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b2);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b2MouseClicked

    private void b3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b3);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b3MouseClicked

    private void b4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b4MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b4);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b4MouseClicked

    private void b5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b5MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b5);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b5MouseClicked

    private void b6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b6MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b6);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b6MouseClicked

    private void b7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b7MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b7);
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b7MouseClicked

    private void b8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b8MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b8);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b8MouseClicked

    private void b9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b9MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b9);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b9MouseClicked

    private void b10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b10MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b10);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b10MouseClicked

    private void b11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b11MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b11);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b11MouseClicked

    private void b12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b12MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(b12);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b12MouseClicked

    private void b13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b13MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b13);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b13MouseClicked

    private void b14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b14MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b14);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b14MouseClicked

    private void b15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_b15MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(b15);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_b15MouseClicked

    private void i1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(i1);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i1MouseClicked

    private void i2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(i2);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i2MouseClicked

    private void i3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(i3);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i3MouseClicked

    private void i4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i4MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(i4);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i4MouseClicked

    private void i5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i5MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                setColorNumeroSeleccionado(i5);
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i5MouseClicked

    private void i6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i6MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i6);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i6MouseClicked

    private void i7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i7MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i7);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i7MouseClicked

    private void i8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i8MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i8);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i8MouseClicked

    private void i9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i9MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i9);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i9MouseClicked

    private void i10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i10MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i10);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i10MouseClicked

    private void i11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i11MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i11);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i11MouseClicked

    private void i12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i12MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i12);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i12MouseClicked

    private void i13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i13MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i13);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i13MouseClicked

    private void i14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i14MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i14);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i14MouseClicked

    private void i15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_i15MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(i15);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_i15MouseClicked

    private void n1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n1);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n1MouseClicked

    private void n2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n2);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n2MouseClicked

    private void n3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n3);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n3MouseClicked

    private void n4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n4MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n4);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n4MouseClicked

    private void n5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n5MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n5);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n5MouseClicked

    private void n6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n6MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n6);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n6MouseClicked

    private void n7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n7MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n7);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n7MouseClicked

    private void n8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n8MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n8);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n8MouseClicked

    private void n9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n9MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n9);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n9MouseClicked

    private void n10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n10MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n10);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n10MouseClicked

    private void n11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n11MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n11);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n11MouseClicked

    private void n12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n12MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n12);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n12MouseClicked

    private void n13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n13MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n13);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n13MouseClicked

    private void n14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n14MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n14);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n14MouseClicked

    private void n15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_n15MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(n15);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_n15MouseClicked

    private void g1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g1);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g1MouseClicked

    private void g2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g2);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g2MouseClicked

    private void g3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g3);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g3MouseClicked

    private void g4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g4MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g4);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g4MouseClicked

    private void g5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g5MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g5);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g5MouseClicked

    private void g6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g6MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g6);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g6MouseClicked

    private void g7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g7MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g7);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g7MouseClicked

    private void g8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g8MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g8);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g8MouseClicked

    private void g9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g9MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g9);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g9MouseClicked

    private void g10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g10MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g10);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g10MouseClicked

    private void g11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g11MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g11);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g11MouseClicked

    private void g12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g12MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g12);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g12MouseClicked

    private void g13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g13MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g13);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g13MouseClicked

    private void g14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g14MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g14);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g14MouseClicked

    private void g15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_g15MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(g15);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_g15MouseClicked

    private void o1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o1);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o1MouseClicked

    private void o2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o2);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o2MouseClicked

    private void o3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o3MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o3);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o3MouseClicked

    private void o4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o4MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o4);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o4MouseClicked

    private void o5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o5MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o5);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o5MouseClicked

    private void o6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o6MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o6);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o6MouseClicked

    private void o7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o7MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o7);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o7MouseClicked

    private void o8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o8MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o8);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o8MouseClicked

    private void o9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o9MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o9);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o9MouseClicked

    private void o10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o10MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o10);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o10MouseClicked

    private void o11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o11MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o11);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o11MouseClicked

    private void o12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o12MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o12);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o12MouseClicked

    private void o13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o13MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o13);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o13MouseClicked

    private void o14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o14MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o14);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o14MouseClicked

    private void o15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_o15MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            try {
                try {
                    setColorNumeroSeleccionado(o15);
                } catch (IOException ex) {
                    Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_o15MouseClicked

    private void cambiaTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiaTituloActionPerformed
        // TODO add your handling code here:

        if (titulo.getText().length() == 5) {
            T1.setText(titulo.getText().substring(0, 1));
            T2.setText(titulo.getText().substring(1, 2));
            T3.setText(titulo.getText().substring(2, 3));
            T4.setText(titulo.getText().substring(3, 4));
            T5.setText(titulo.getText().substring(4, 5));
        } else {
            T1.setText("B");
            T2.setText("I");
            T3.setText("N");
            T4.setText("G");
            T5.setText("O");
        }

    }//GEN-LAST:event_cambiaTituloActionPerformed

    private void colorNumeroSeleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorNumeroSeleccionadoActionPerformed
        // TODO add your handling code here:
        Color color = null;

        color = JColorChooser.showDialog(null, "Color de Texto de Titulo", color);
        if (color == null) {
            color = Color.WHITE;
        }
        cNumeroSeleccionado = color;

    }//GEN-LAST:event_colorNumeroSeleccionadoActionPerformed

    private void colorNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorNumeroActionPerformed
        // TODO add your handling code here:

        // TODO add your handling code here:
        Color color = null;

        color = JColorChooser.showDialog(null, "Color de Texto de Titulo", color);
        if (color == null) {
            color = Color.WHITE;
        }
        cNumero = color;
        this.setColorNumeros(cNumero);

    }//GEN-LAST:event_colorNumeroActionPerformed

    private void colorTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorTituloActionPerformed
        // TODO add your handling code here:
        Color color = null;

        color = JColorChooser.showDialog(null, "Color de Texto de Titulo", color);
        if (color == null) {
            color = Color.WHITE;
        }

        cTitulo = color;

        T1.setForeground(color);
        T2.setForeground(color);
        T3.setForeground(color);
        T4.setForeground(color);
        T5.setForeground(color);
    }//GEN-LAST:event_colorTituloActionPerformed

    private void colorFondoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFondoActionPerformed
        // TODO add your handling code here:

        // TODO add your handling code here:
        Color color = null;

        color = JColorChooser.showDialog(null, "Color de Fondo de Titulo", color);
        if (color == null) {
            color = Color.WHITE;
        }
        cFondo = color;
        this.setColorFondo(cFondo);

    }//GEN-LAST:event_colorFondoActionPerformed

    private void tituloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tituloKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_tituloKeyPressed

    private void tituloPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tituloPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tituloPropertyChange

    private void tituloInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tituloInputMethodTextChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_tituloInputMethodTextChanged

    private void reiniciarBingoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reiniciarBingoActionPerformed
        // TODO add your handling code here:
        index = 1;
        vWinTablas.removeAllElements();
        vBingo.removeAllElements();
        vBingo.addElement("-1");
        ganadores.removeAllElements();

        cTitulo = Color.RED;
        cNumero = Color.BLUE;
        cFondo = Color.BLACK;
        cNumeroSeleccionado = Color.YELLOW;

        this.setColorFondo(cFondo);
        this.setColorNumeros(cNumero);
        this.setColorTitulo(cTitulo);
        
        panelConfiguracion.setVisible(false);
        this.mostrarPanel.setVisible(true);
        
    }//GEN-LAST:event_reiniciarBingoActionPerformed

    private void juegoLetraUGrandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoLetraUGrandeActionPerformed
        // TODO add your handling code here:
        if (this.juegoLetraUGrande.isSelected()) vImagenes.add("Letra_U_Grande.png");
        else vImagenes.remove("Letra_U_Grande.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoLetraUGrandeActionPerformed

    private void colorFondoGanadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFondoGanadoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_colorFondoGanadoresActionPerformed

    private void colorTituloGanadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorTituloGanadoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_colorTituloGanadoresActionPerformed

    private void colorNumeroTablasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorNumeroTablasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_colorNumeroTablasActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Muestra la MISMA ventana de historial estilizada (verde/amarillo, agrupada)
        // que aparece al ganar, pero con boton "Cerrar" y habilitada 10 segundos.
        if (ganadores == null || ganadores.isEmpty()) return;
        win = new WinPantalla(this, true, ganadores, 10, true);
        win.setSize(800, 800);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void mostrarPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarPanelActionPerformed
        // TODO add your handling code here:
        panelConfiguracion.setVisible(true);
        this.mostrarPanel.setVisible(false);
    }//GEN-LAST:event_mostrarPanelActionPerformed

    private void juegoPlenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoPlenoActionPerformed
        // TODO add your handling code here:
        if (juegoPleno.isSelected()) vImagenes.add("Pleno.png");
        else vImagenes.remove("Pleno.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoPlenoActionPerformed

    private void juegoCruzPequeñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoCruzPequeñaActionPerformed
        // TODO add your handling code here:
        if (this.juegoCruzPequeña.isSelected()) vImagenes.add("Cruz_Pequena.png");
        else vImagenes.remove("Cruz_Pequena.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoCruzPequeñaActionPerformed

    private void juegoCruzGrandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoCruzGrandeActionPerformed
        // TODO add your handling code here:
        if (this.juegoCruzGrande.isSelected()) vImagenes.add("Cruz_Grande.png");
        else vImagenes.remove("Cruz_Grande.png");
        this.mostrarImagenes();

    }//GEN-LAST:event_juegoCruzGrandeActionPerformed

    private void juegoCuatroEsquinasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoCuatroEsquinasActionPerformed
        // TODO add your handling code here:
        if (this.juegoCuatroEsquinas.isSelected()) vImagenes.add("Cuatro_Esquinas.png");
        else vImagenes.remove("Cuatro_Esquinas.png");
        this.mostrarImagenes();

    }//GEN-LAST:event_juegoCuatroEsquinasActionPerformed

    private void juegoMachetazoIzquierdoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoMachetazoIzquierdoActionPerformed
        // TODO add your handling code here:
        if (this.juegoMachetazoIzquierdo.isSelected()) vImagenes.add("Machetazo_Izquierdo.png");
        else vImagenes.remove("Machetazo_Izquierdo.png");
        this.mostrarImagenes();

    }//GEN-LAST:event_juegoMachetazoIzquierdoActionPerformed

    private void juegoMachetazoDerechoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoMachetazoDerechoActionPerformed
        // TODO add your handling code here:
        if (this.juegoMachetazoDerecho.isSelected()) vImagenes.add("Machetazo_Derecho.png");
        else vImagenes.remove("Machetazo_Derecho.png");
        this.mostrarImagenes();

    }//GEN-LAST:event_juegoMachetazoDerechoActionPerformed

    private void juegoPuntaFlechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoPuntaFlechaActionPerformed
        // TODO add your handling code here:
        if (this.juegoPuntaFlecha.isSelected()) vImagenes.add("Punta_Fecha.png");
        else vImagenes.remove("Punta_Fecha.png");
        this.mostrarImagenes();

    }//GEN-LAST:event_juegoPuntaFlechaActionPerformed

    private void juegoVerticalCentralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoVerticalCentralActionPerformed
        // TODO add your handling code here:
        if (this.juegoVerticalCentral.isSelected()) vImagenes.add("Vertical_Central.png");
        else vImagenes.remove("Vertical_Central.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoVerticalCentralActionPerformed

    private void juegoHorizontalCentralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoHorizontalCentralActionPerformed
        // TODO add your handling code here:
        if (this.juegoHorizontalCentral.isSelected()) vImagenes.add("Horizontal_Central.png");
        else vImagenes.remove("Horizontal_Central.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoHorizontalCentralActionPerformed

    private void juegoLetraTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoLetraTActionPerformed
        // TODO add your handling code here:
        if (this.juegoLetraT.isSelected()) vImagenes.add("Letra_T.png");
        else vImagenes.remove("Letra_T.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoLetraTActionPerformed

    private void juegoLetraLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoLetraLActionPerformed
        // TODO add your handling code here:
        if (this.juegoLetraL.isSelected()) vImagenes.add("Letra_L.png");
        else vImagenes.remove("Letra_L.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoLetraLActionPerformed

    private void juegoLetraXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoLetraXActionPerformed
        // TODO add your handling code here:
        if (this.juegoLetraX.isSelected()) vImagenes.add("Letra_X.png");
        else vImagenes.remove("Letra_X.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoLetraXActionPerformed

    private void juegoLetraUPequeñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juegoLetraUPequeñaActionPerformed
        // TODO add your handling code here:
        if (this.juegoLetraUPequeña.isSelected()) vImagenes.add("Letra_U_Pequena.png");
        else vImagenes.remove("Letra_U_Pequena.png");
        this.mostrarImagenes();
    }//GEN-LAST:event_juegoLetraUPequeñaActionPerformed

    private void juegoArchivoActionPerformed(java.awt.event.ActionEvent evt) {
        // Atajo: marca/desmarca TODAS las figuras de archivo (la seleccion fina
        // esta en la pestaña "Figuras").
        if (figChecks != null) {
            for (javax.swing.JCheckBox c : figChecks) c.setSelected(this.juegoArchivo.isSelected());
        }
        this.mostrarImagenes();
    }

    private void btn_SalirGeneradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SalirGeneradorActionPerformed
        // TODO add your handling code here:
        
       System.exit(0);
    }//GEN-LAST:event_btn_SalirGeneradorActionPerformed

    private void btn_updPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updPasswordActionPerformed
        // TODO add your handling code here:
                winUpdPassword.setVisible(true);

        
    }//GEN-LAST:event_btn_updPasswordActionPerformed

    private void mostrarMensaje() {
        int seg = 4;
        try { seg = Integer.parseInt(this.segundoMensaje.getText().trim()); }
        catch (Exception ex) { seg = 4; }
        if (seg <= 0) seg = 4;
        if (ganadores == null || ganadores.isEmpty()) return;

        // 1) Flash "¡GANADOR!" (4s, animado) del ganador ACTUAL.
        try {
            Ganador actual = ganadores.get(ganadores.size() - 1);
            String juegoActual = actual.getJuego();
            java.util.List<String> tablasActual = new java.util.ArrayList<>();
            for (int i = 0; i < ganadores.size(); i++)
                if (juegoActual.equals(ganadores.get(i).getJuego()))
                    tablasActual.add(ganadores.get(i).getCodigo());
            FlashGanador flash = new FlashGanador(this, juegoActual, tablasActual, patronDeJuego(juegoActual));
            flash.iniciar();   // modal: bloquea 4s y hace fade-out -> transicion
        } catch (Throwable t) {
            // si el flash falla, continuamos directo al historial
        }

        // 2) Ventana de historial (aparece tras el flash).
        win = new WinPantalla(this, true, ganadores, seg);
        win.setSize(800, 800);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
    }

    /** boolean[25] del patron de la figura cuyo nombre-a-mostrar coincide con 'juego'. */
    private boolean[] patronDeJuego(String juego) {
        boolean[] p = new boolean[25];
        if (matrices == null || juego == null) return p;
        for (int fi = 0; fi < matrices.size(); fi++) {
            if (juego.equals(nombreMostrarFigura(fi))) {
                for (Integer pos : posicionesDeMatriz(matrices.get(fi)))
                    if (pos != null && pos >= 0 && pos < 25) p[pos] = true;
                break;
            }
        }
        return p;
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Fondo;
    private javax.swing.JLabel T1;
    private javax.swing.JLabel T2;
    private javax.swing.JLabel T3;
    private javax.swing.JLabel T4;
    private javax.swing.JLabel T5;
    private javax.swing.JLabel b1;
    private javax.swing.JLabel b10;
    private javax.swing.JLabel b11;
    private javax.swing.JLabel b12;
    private javax.swing.JLabel b13;
    private javax.swing.JLabel b14;
    private javax.swing.JLabel b15;
    private javax.swing.JLabel b2;
    private javax.swing.JLabel b3;
    private javax.swing.JLabel b4;
    private javax.swing.JLabel b5;
    private javax.swing.JLabel b6;
    private javax.swing.JLabel b7;
    private javax.swing.JLabel b8;
    private javax.swing.JLabel b9;
    private javax.swing.JButton btn_SalirGenerador;
    private javax.swing.JButton btn_updPassword;
    private javax.swing.JButton cambiaTitulo;
    private javax.swing.JButton colorFondo;
    private javax.swing.JButton colorFondoGanadores;
    private javax.swing.JButton colorNumero;
    private javax.swing.JButton colorNumeroSeleccionado;
    private javax.swing.JButton colorNumeroTablas;
    private javax.swing.JButton colorTitulo;
    private javax.swing.JButton colorTituloGanadores;
    private javax.swing.JLabel g1;
    private javax.swing.JLabel g10;
    private javax.swing.JLabel g11;
    private javax.swing.JLabel g12;
    private javax.swing.JLabel g13;
    private javax.swing.JLabel g14;
    private javax.swing.JLabel g15;
    private javax.swing.JLabel g2;
    private javax.swing.JLabel g3;
    private javax.swing.JLabel g4;
    private javax.swing.JLabel g5;
    private javax.swing.JLabel g6;
    private javax.swing.JLabel g7;
    private javax.swing.JLabel g8;
    private javax.swing.JLabel g9;
    private javax.swing.JLabel i1;
    private javax.swing.JLabel i10;
    private javax.swing.JLabel i11;
    private javax.swing.JLabel i12;
    private javax.swing.JLabel i13;
    private javax.swing.JLabel i14;
    private javax.swing.JLabel i15;
    private javax.swing.JLabel i2;
    private javax.swing.JLabel i3;
    private javax.swing.JLabel i4;
    private javax.swing.JLabel i5;
    private javax.swing.JLabel i6;
    private javax.swing.JLabel i7;
    private javax.swing.JLabel i8;
    private javax.swing.JLabel i9;
    private javax.swing.JLabel imagen1;
    private javax.swing.JLabel imagen2;
    private javax.swing.JLabel imagen3;
    private javax.swing.JLabel imagen4;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JCheckBox juegoArchivo;
    private javax.swing.JCheckBox juegoCruzGrande;
    private javax.swing.JCheckBox juegoCruzPequeña;
    private javax.swing.JCheckBox juegoCuatroEsquinas;
    private javax.swing.JCheckBox juegoHorizontalCentral;
    private javax.swing.JCheckBox juegoLetraL;
    private javax.swing.JCheckBox juegoLetraT;
    private javax.swing.JCheckBox juegoLetraUGrande;
    private javax.swing.JCheckBox juegoLetraUPequeña;
    private javax.swing.JCheckBox juegoLetraX;
    private javax.swing.JCheckBox juegoMachetazoDerecho;
    private javax.swing.JCheckBox juegoMachetazoIzquierdo;
    private javax.swing.JCheckBox juegoPleno;
    private javax.swing.JCheckBox juegoPuntaFlecha;
    private javax.swing.JCheckBox juegoVerticalCentral;
    private javax.swing.JButton mostrarPanel;
    private javax.swing.JLabel n1;
    private javax.swing.JLabel n10;
    private javax.swing.JLabel n11;
    private javax.swing.JLabel n12;
    private javax.swing.JLabel n13;
    private javax.swing.JLabel n14;
    private javax.swing.JLabel n15;
    private javax.swing.JLabel n2;
    private javax.swing.JLabel n3;
    private javax.swing.JLabel n4;
    private javax.swing.JLabel n5;
    private javax.swing.JLabel n6;
    private javax.swing.JLabel n7;
    private javax.swing.JLabel n8;
    private javax.swing.JLabel n9;
    private javax.swing.JLabel o1;
    private javax.swing.JLabel o10;
    private javax.swing.JLabel o11;
    private javax.swing.JLabel o12;
    private javax.swing.JLabel o13;
    private javax.swing.JLabel o14;
    private javax.swing.JLabel o15;
    private javax.swing.JLabel o2;
    private javax.swing.JLabel o3;
    private javax.swing.JLabel o4;
    private javax.swing.JLabel o5;
    private javax.swing.JLabel o6;
    private javax.swing.JLabel o7;
    private javax.swing.JLabel o8;
    private javax.swing.JLabel o9;
    private javax.swing.JTabbedPane panelConfiguracion;
    private javax.swing.JButton reiniciarBingo;
    private javax.swing.JTextField segundoMensaje;
    private javax.swing.JTextField titulo;
    // End of variables declaration//GEN-END:variables

    private String String(char c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setColorFondo(Color color) {
        Fondo.setBackground(color);
    }

    public void setColorTitulo(Color color) {
        T1.setForeground(color);
        T2.setForeground(color);
        T3.setForeground(color);
        T4.setForeground(color);
        T5.setForeground(color);
    }

    public void setColorNumeros(Color color) {

        b1.setForeground(color);
        b2.setForeground(color);
        b3.setForeground(color);
        b4.setForeground(color);
        b5.setForeground(color);
        b6.setForeground(color);
        b7.setForeground(color);
        b8.setForeground(color);
        b9.setForeground(color);
        b10.setForeground(color);
        b11.setForeground(color);
        b12.setForeground(color);
        b13.setForeground(color);
        b14.setForeground(color);
        b15.setForeground(color);

        i1.setForeground(color);
        i2.setForeground(color);
        i3.setForeground(color);
        i4.setForeground(color);
        i5.setForeground(color);
        i6.setForeground(color);
        i7.setForeground(color);
        i8.setForeground(color);
        i9.setForeground(color);
        i10.setForeground(color);
        i11.setForeground(color);
        i12.setForeground(color);
        i13.setForeground(color);
        i14.setForeground(color);
        i15.setForeground(color);

        n1.setForeground(color);
        n2.setForeground(color);
        n3.setForeground(color);
        n4.setForeground(color);
        n5.setForeground(color);
        n6.setForeground(color);
        n7.setForeground(color);
        n8.setForeground(color);
        n9.setForeground(color);
        n10.setForeground(color);
        n11.setForeground(color);
        n12.setForeground(color);
        n13.setForeground(color);
        n14.setForeground(color);
        n15.setForeground(color);

        g1.setForeground(color);
        g2.setForeground(color);
        g3.setForeground(color);
        g4.setForeground(color);
        g5.setForeground(color);
        g6.setForeground(color);
        g7.setForeground(color);
        g8.setForeground(color);
        g9.setForeground(color);
        g10.setForeground(color);
        g11.setForeground(color);
        g12.setForeground(color);
        g13.setForeground(color);
        g14.setForeground(color);
        g15.setForeground(color);

        o1.setForeground(color);
        o2.setForeground(color);
        o3.setForeground(color);
        o4.setForeground(color);
        o5.setForeground(color);
        o6.setForeground(color);
        o7.setForeground(color);
        o8.setForeground(color);
        o9.setForeground(color);
        o10.setForeground(color);
        o11.setForeground(color);
        o12.setForeground(color);
        o13.setForeground(color);
        o14.setForeground(color);
        o15.setForeground(color);
    }

    public void setColorNumeroSeleccionado(JLabel num) throws SQLException, IOException {
        Color colortmp = null;
        Vector<Ganador> tmpGanadores = new Vector<Ganador>();
        colortmp = num.getForeground();
        if (colortmp == cNumero) {
            num.setForeground(cNumeroSeleccionado);
            vBingo.addElement(num.getText());

            // verificar los juegos seleccionados
            if (this.juegoPleno.isSelected()) {
                if ((tmpGanadores = modoProgramado
                        ? con.verificarPleno(vBingo, pT01, pT02, pT03, codTabla10, codTabla11)
                        : con.verificarPleno(vBingo)).size() != 0 && !buscarGanadores(ganadores, "Pleno")) {
                    unirGanadores(ganadores, tmpGanadores);
                    tmpGanadores.removeAllElements();
//                JOptionPane.showMessageDialog(this, "Ganaste Pleno.", "Ganador", JOptionPane.INFORMATION_MESSAGE);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoCruzPequeña.isSelected() && !buscarGanadores(ganadores, "Cruz Pequeña")) {
                if ((tmpGanadores = con.verificarCruzPequeña(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    tmpGanadores.removeAllElements();
                    //              JOptionPane.showMessageDialog(this, "Ganaste Cruz Pequeña.", "Ganador", JOptionPane.INFORMATION_MESSAGE);
                    this.mostrarMensaje();

                }
            }

            if (this.juegoCruzGrande.isSelected() && !buscarGanadores(ganadores, "Cruz Grande")) {
                if ((tmpGanadores = con.verificarCruzGrande(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();

                }
            }

            if (this.juegoCuatroEsquinas.isSelected() && !buscarGanadores(ganadores, "Cuatro Esquinas")) {
                if ((tmpGanadores = con.verificarCuatroEsquinas(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoMachetazoIzquierdo.isSelected() && !buscarGanadores(ganadores, "Machetazo Izquierdo")) {
                if ((tmpGanadores = con.verificarMachetazoIzquierdo(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoMachetazoDerecho.isSelected() && !buscarGanadores(ganadores, "Machetazo Derecho")) {
                if ((tmpGanadores = con.verificarMachetazoDerecho(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoPuntaFlecha.isSelected() && !buscarGanadores(ganadores, "Punta de Flecha")) {
                if ((tmpGanadores = con.verificarPuntadeFlecha(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoVerticalCentral.isSelected() && !buscarGanadores(ganadores, "Vertical Central")) {
                if ((tmpGanadores = con.verificarVerticalCentral(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoHorizontalCentral.isSelected() && !buscarGanadores(ganadores, "Horizontal Central")) {
                if ((tmpGanadores = con.verificarHorizontalCentral(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoLetraT.isSelected() && !buscarGanadores(ganadores, "Letra T")) {
                if ((tmpGanadores = modoProgramado
                        ? con.verificarLetraT(vBingo, codTabla30, codTabla31)
                        : con.verificarLetraT(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            if (this.juegoLetraL.isSelected() && !buscarGanadores(ganadores, "Letra L")) {
                if ((tmpGanadores = modoProgramado
                        ? con.verificarLetraL(vBingo, codTabla40, codTabla41)
                        : con.verificarLetraL(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }
            if (this.juegoLetraX.isSelected() && !buscarGanadores(ganadores, "Letra X")) {
                if ((tmpGanadores = modoProgramado
                        ? con.verificarLetraX(vBingo, codTabla50, codTabla51)
                        : con.verificarLetraX(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }
            if (this.juegoLetraUGrande.isSelected() && !buscarGanadores(ganadores, "Letra U Grande")) {
                if ((tmpGanadores = modoProgramado
                        ? con.verificarLetraUGrande(vBingo, codTabla20, codTabla21)
                        : con.verificarLetraUGrande(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }
            if (this.juegoLetraUPequeña.isSelected() && !buscarGanadores(ganadores, "Letra U Pequeña")) {
                if ((tmpGanadores = con.verificarLetraUPequeña(vBingo)).size() != 0) {
                    unirGanadores(ganadores, tmpGanadores);
                    this.mostrarMensaje();
                }
            }

            // Figuras configurables desde matriz.txt: SOLO las seleccionadas por el usuario.
            // El Ganador lleva el NOMBRE A MOSTRAR (traduccion) de la figura.
            boolean nuevoGanadorArchivo = false;
            if (matrices != null && figChecks != null) {
                for (int fi = 0; fi < matrices.size() && fi < figChecks.size(); fi++) {
                    if (!figChecks.get(fi).isSelected()) continue;
                    String nombreFig = nombreMostrarFigura(fi);
                    if (buscarGanadores(ganadores, nombreFig)) continue;
                    java.util.List<Integer> posiciones = posicionesDeMatriz(matrices.get(fi));
                    if ((tmpGanadores = con.verificarArchivo(vBingo, posiciones, nombreFig)).size() != 0) {
                        unirGanadores(ganadores, tmpGanadores);
                        nuevoGanadorArchivo = true;
                    }
                }
            }

            // Una SOLA ventana con TODOS los ganadores (no una por figura).
            if (nuevoGanadorArchivo) {
                this.mostrarMensaje();
            }

            // Cada figura ganada desaparece de la tira de orientacion.
            this.sincronizarFigurasEnLinea();

        } else {
            num.setForeground(cNumero);
            vBingo.removeElement(num.getText());
        }
    }

    public Vector<Ganador> unirGanadores(Vector<Ganador> ori, Vector<Ganador> tmp) {
        for (int i = 0; i < tmp.size(); i++) {
            ori.add(tmp.get(i));
        }

        return ori;
    }

    public boolean buscarGanadores(Vector<Ganador> ori, String juego) {
        for (int i = 0; i < ori.size(); i++) {
            if (ori.get(i).getJuego().equals(juego)) {
                return true;
            }
        }
        return false;
    }

    private void mostrarImagenes() {
        if (tiraFiguras == null) return;
        tiraFiguras.removeAll();
        // Tira horizontal: figuras SELECCIONADAS y aun NO ganadas (se quitan al ganar).
        if (matrices != null && figChecks != null) {
            for (int fi = 0; fi < matrices.size() && fi < figChecks.size(); fi++) {
                if (!figChecks.get(fi).isSelected()) continue;
                if (buscarGanadores(ganadores, nombreMostrarFigura(fi))) continue;
                javax.swing.JLabel lab = new javax.swing.JLabel(iconoDePatron(matrices.get(fi), 150));
                lab.setToolTipText((nombresFiguras != null && fi < nombresFiguras.size())
                        ? nombresFiguras.get(fi) : "");
                lab.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 6, 4, 6));
                tiraFiguras.add(lab);
            }
        }
        tiraFiguras.revalidate();
        tiraFiguras.repaint();
    }

    /** Nombre a mostrar (traduccion) de la figura fi; si no tiene, el interno. */
    private String nombreMostrarFigura(int fi) {
        if (nombresMostrar != null && fi < nombresMostrar.size()
                && nombresMostrar.get(fi) != null && !nombresMostrar.get(fi).trim().isEmpty())
            return nombresMostrar.get(fi).trim();
        if (nombresFiguras != null && fi < nombresFiguras.size())
            return nombresFiguras.get(fi);
        return "Figura " + (fi + 1);
    }

    /** Reemplaza la 1a imagen de orientacion por una tira horizontal con scroll. */
    private void construirTiraFiguras() {
        try {
            tiraFiguras = new javax.swing.JPanel();
            tiraFiguras.setLayout(new javax.swing.BoxLayout(tiraFiguras, javax.swing.BoxLayout.X_AXIS));
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(tiraFiguras,
                    javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                    javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            sp.getHorizontalScrollBar().setUnitIncrement(24);
            sp.setBorder(null);
            java.awt.LayoutManager lm = getContentPane().getLayout();
            if (lm instanceof javax.swing.GroupLayout) {
                ((javax.swing.GroupLayout) lm).replace(imagen1, sp);
            }
            imagen2.setVisible(false);
            imagen3.setVisible(false);
            imagen4.setVisible(false);
        } catch (Exception ignore) {
        }
    }

    /**
     * Devuelve el archivo de imagen (orientacion del juego) que corresponde
     * al nombre interno de una figura. null si no hay correspondencia.
     */
    private String imagenDeFigura(String juego) {
        if (juego == null) return null;
        switch (juego) {
            case "Pleno":               return "Pleno.png";
            case "Cruz Pequeña":        return "Cruz_Pequena.png";
            case "Cruz Grande":         return "Cruz_Grande.png";
            case "Cuatro Esquinas":     return "Cuatro_Esquinas.png";
            case "Machetazo Izquierdo": return "Machetazo_Izquierdo.png";
            case "Machetazo Derecho":   return "Machetazo_Derecho.png";
            case "Punta de Flecha":     return "Punta_Fecha.png";
            case "Vertical Central":    return "Vertical_Central.png";
            case "Horizontal Central":  return "Horizontal_Central.png";
            case "Letra T":             return "Letra_T.png";
            case "Letra L":             return "Letra_L.png";
            case "Letra X":             return "Letra_X.png";
            case "Letra U Pequeña":     return "Letra_U_Pequena.png";
            case "Letra U Grande":      return "Letra_U_Grande.png";
            default:                    return null;
        }
    }

    /**
     * Oculta de la pantalla de orientacion la imagen de cada figura que ya
     * tiene ganador, dejando visibles solo los juegos que siguen en curso.
     * Al ganarse todos los juegos elegidos la pantalla queda limpia.
     */
    private void sincronizarFigurasEnLinea() {
        for (int i = 0; i < ganadores.size(); i++) {
            String img = imagenDeFigura(ganadores.get(i).getJuego());
            if (img != null) {
                vImagenes.remove(img);
            }
        }
        // Las figuras de archivo ganadas se ocultan solas: mostrarImagenes()
        // omite las que ya tienen ganador ("Figura archivo N").
        this.mostrarImagenes();
    }

    // =====================================================================
    // Soporte de figuras configurables desde archivo (matriz.txt)
    // =====================================================================

    /** Establece las figuras (matrices de texto) cargadas del archivo. */
    public void setFiguras(java.util.List<String> figuras) {
        this.matrices = (figuras != null) ? figuras : new java.util.ArrayList<String>();
    }

    /** Figuras (matrices de texto) actualmente cargadas. */
    public java.util.List<String> getFiguras() {
        return this.matrices;
    }

    /**
     * Convierte una matriz 5x5 de texto ('X'/'0', filas separadas por '\n') en
     * la lista de índices de celda marcados, en el mismo orden column-major
     * (fila + 5*col) que usa getVectorBingo. Idéntico a la línea v01.
     */
    private java.util.List<Integer> posicionesDeMatriz(String figura) {
        java.util.List<Integer> posiciones = new java.util.ArrayList<>();
        if (figura == null) return posiciones;
        int fila = 0, incremento = 0, posicion = 0;
        for (int caracter = 0; caracter < figura.length(); caracter++) {
            posicion = posicion + incremento;
            char ch = figura.charAt(caracter);
            if (ch == 'X' || ch == 'x') {
                posiciones.add(posicion);
            } else if (ch == '\n') {
                fila++; posicion = fila; incremento = 0; continue;
            }
            incremento = 5;
        }
        return posiciones;
    }

    /** Renderiza una matriz 5x5 ('X'/'0') a un icono (celdas 'X' resaltadas). */
    private ImageIcon iconoDePatron(String matriz, int px) {
        int celda = Math.max(8, px / 5);
        int lado = celda * 5;
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(lado, lado, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, lado, lado);
        String[] filas = (matriz == null) ? new String[0] : matriz.split("\n");
        for (int r = 0; r < filas.length && r < 5; r++) {
            String fila = filas[r];
            for (int c = 0; c < fila.length() && c < 5; c++) {
                boolean marcada = (fila.charAt(c) == 'X' || fila.charAt(c) == 'x');
                g.setColor(marcada ? Color.YELLOW : new Color(45, 45, 45));
                g.fillRect(c * celda + 1, r * celda + 1, celda - 2, celda - 2);
            }
        }
        g.setColor(Color.DARK_GRAY);
        for (int k = 0; k <= 5; k++) {
            g.drawLine(k * celda, 0, k * celda, lado);
            g.drawLine(0, k * celda, lado, k * celda);
        }
        g.dispose();
        return new ImageIcon(img);
    }

}
