package Vista;

import Controlador.CatalogoFiguras;
import Controlador.Preferencias;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Pestaña "Figuras": configura las tablas ajustadas de la partida programada
 * para CADA figura del catalogo, no para una lista fija.
 *
 * <p>Reemplaza al esquema anterior, donde el formulario tenia 16 filas dibujadas
 * a mano y {@code config.ker} las guardaba por posicion. Aqui las filas se
 * generan leyendo {@code /Bingo/db/matriz.txt} ({@link CatalogoFiguras}), asi
 * que aparecen todas las figuras que existan: si mañana se agrega una al
 * archivo, aparece sola, sin tocar el programa.</p>
 *
 * <p>Se guarda con claves {@code figura.&lt;nombre&gt;.tabla1|tabla2|balotas}.
 * La Pantalla las lee por nombre al detectar la figura.</p>
 */
public class FigurasDinamicas extends JPanel {

    private static final int COL_FIGURA = 0;
    private static final int COL_MUESTRA = 1;
    private static final int COL_CASILLAS = 2;
    private static final int COL_TABLA1 = 3;
    private static final int COL_TABLA2 = 4;
    private static final int COL_COMPLETA1 = 5;
    private static final int COL_COMPLETA2 = 6;
    private static final int COL_COMPLETA3 = 7;

    private final Preferencias prefs;
    private final List<CatalogoFiguras.Figura> figuras;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JLabel estado = new JLabel(" ");

    public FigurasDinamicas(Preferencias prefs) {
        this.prefs = prefs;
        this.figuras = CatalogoFiguras.leer();

        JLabel titulo = new JLabel("Figuras de la partida programada", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));

        JLabel ayuda = new JLabel("<html><center>Tablas que deben resultar premiadas en cada figura, en dos momentos:<br>"
                + "<b>Falta 1</b> — cuando al carton le falta una casilla &nbsp;|&nbsp; "
                + "<b>Completa</b> — cuando la figura se completa.<br>"
                + "Deje en blanco las figuras que no quiera ajustar.</center></html>",
                SwingConstants.CENTER);
        ayuda.setForeground(Color.DARK_GRAY);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(ayuda, BorderLayout.CENTER);
        norte.setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));

        modelo = new DefaultTableModel(
                new Object[]{"Figura", "Se muestra como", "Casillas",
                    "Falta 1 → T1", "Falta 1 → T2",
                    "Completa → T1", "Completa → T2", "Completa → T3"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna >= COL_TABLA1;       // solo las tablas
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        anchos();

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar();
            }
        });
        JButton btnCargar = new JButton("Recargar");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefs.cargar();
                llenar();
                estado.setText("Recargado desde " + Preferencias.getRutaArchivo());
            }
        });
        JButton btnLimpiar = new JButton("Quitar todos los ajustes");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiar();
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.add(btnGuardar);
        botones.add(btnCargar);
        botones.add(btnLimpiar);

        estado.setHorizontalAlignment(SwingConstants.CENTER);
        estado.setFont(estado.getFont().deriveFont(Font.PLAIN, 11f));
        estado.setForeground(Color.GRAY);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.CENTER);
        sur.add(estado, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(720, 460));

        llenar();
    }

    private void anchos() {
        int[] w = {180, 150, 65, 95, 95, 105, 105, 105};
        for (int i = 0; i < w.length && i < tabla.getColumnCount(); i++) {
            TableColumn c = tabla.getColumnModel().getColumn(i);
            c.setPreferredWidth(w[i]);
        }
    }

    /** Vuelca el catalogo + lo guardado en la tabla. */
    private void llenar() {
        modelo.setRowCount(0);
        for (CatalogoFiguras.Figura f : figuras) {
            String[] t = prefs.getTablasFigura(f.getNombre());
            String[] c = prefs.getCompletaFigura(f.getNombre());
            modelo.addRow(new Object[]{
                f.getNombre(),
                f.getMostrar(),
                f.getCasillas().size(),
                "-1".equals(t[0]) ? "" : t[0],
                "-1".equals(t[1]) ? "" : t[1],
                "-1".equals(c[0]) ? "" : c[0],
                "-1".equals(c[1]) ? "" : c[1],
                "-1".equals(c[2]) ? "" : c[2]
            });
        }
        if (figuras.isEmpty()) {
            estado.setText("No se pudo leer " + CatalogoFiguras.getArchivo().getAbsolutePath());
        } else {
            estado.setText(figuras.size() + " figuras leidas de " + CatalogoFiguras.getArchivo().getAbsolutePath());
        }
    }

    private void guardar() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }
        prefs.limpiarFiguras();                 // se regraba completo
        // Configurar aqui manda: la partida pasa a jugarse con estas figuras,
        // aunque las tablas se hubieran generado con numeros de excepcion.
        prefs.setModoPartida(Preferencias.MODO_FIGURAS);
        // La partida del esquema viejo queda inalcanzable en cuanto hay figuras
        // configuradas, asi que se va del archivo en vez de quedar de lastre.
        prefs.purgarPartidaVieja();
        prefs.normalizarModulos();
        int conAjuste = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String nombre = texto(modelo.getValueAt(i, COL_FIGURA));
            String t1 = texto(modelo.getValueAt(i, COL_TABLA1));
            String t2 = texto(modelo.getValueAt(i, COL_TABLA2));
            String c1 = texto(modelo.getValueAt(i, COL_COMPLETA1));
            String c2 = texto(modelo.getValueAt(i, COL_COMPLETA2));
            String c3 = texto(modelo.getValueAt(i, COL_COMPLETA3));
            if (t1.isEmpty() && t2.isEmpty() && c1.isEmpty() && c2.isEmpty() && c3.isEmpty()) {
                continue;                       // figura sin configurar
            }
            prefs.setFigura(nombre, t1, t2, 0);
            prefs.setCompletaFigura(nombre, c1, c2, c3);
            conAjuste++;
        }
        if (!prefs.guardar()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo escribir:\n" + Preferencias.getRutaArchivo(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        estado.setText("Guardado: " + conAjuste + " figuras con tablas ajustadas");
        JOptionPane.showMessageDialog(this,
                conAjuste == 0
                        ? "No quedo ninguna figura ajustada: la Pantalla jugara normal."
                        : "Guardadas " + conAjuste + " figuras con tablas ajustadas.",
                "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiar() {
        int op = JOptionPane.showConfirmDialog(this,
                "Se quitaran las tablas ajustadas de TODAS las figuras.\n¿Confirma?",
                "Quitar ajustes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op != JOptionPane.YES_OPTION) {
            return;
        }
        prefs.limpiarFiguras();
        prefs.setModoPartida(Preferencias.MODO_FIGURAS);
        prefs.purgarPartidaVieja();
        prefs.normalizarModulos();
        prefs.guardar();
        llenar();
        estado.setText("Ajustes retirados de todas las figuras");
    }

    private static String texto(Object v) {
        return v == null ? "" : v.toString().trim();
    }

    private static int entero(String s) {
        try {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
