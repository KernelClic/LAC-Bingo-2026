package Vista;

import Controlador.Conector;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Pestaña "Rangos de Tablas": lista las tablas de la base y permite borrar las
 * que se pasen a la lista de la derecha.
 *
 * <p>Vivia dentro de Config01, que era una ventana entera del esquema viejo de
 * configuracion. Al retirarse esas pestañas el panel se quedaba sin casa, asi
 * que se extrajo tal cual: mismos controles, mismo comportamiento y los mismos
 * dos botones. Ya no depende de nada del esquema viejo.</p>
 */
public class RangosTablas extends javax.swing.JPanel {

    private final Conector con;

    private final javax.swing.JTable jTablas = new javax.swing.JTable();
    private final javax.swing.JTable jTablasE = new javax.swing.JTable();
    private final javax.swing.JButton pasarAeliminar = new javax.swing.JButton("Pasar");
    private final javax.swing.JButton eliminarDatos = new javax.swing.JButton("Eliminar");

    public RangosTablas() throws IOException, SQLException {
        con = new Conector();
        con.connect();

        armar();
        cargar();
    }

    private void armar() {
        jTablas.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"No. Tabla", "Codigo"}));
        jTablasE.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"No. Tabla", "Codigo"}));

        javax.swing.JScrollPane spOrigen = new javax.swing.JScrollPane(jTablas);
        javax.swing.JScrollPane spEliminar = new javax.swing.JScrollPane(jTablasE);
        spOrigen.setPreferredSize(new java.awt.Dimension(162, 292));
        spEliminar.setPreferredSize(new java.awt.Dimension(162, 292));

        pasarAeliminar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pasarAEliminar();
            }
        });
        eliminarDatos.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminar();
            }
        });

        javax.swing.JPanel botones = new javax.swing.JPanel();
        botones.setLayout(new javax.swing.BoxLayout(botones, javax.swing.BoxLayout.Y_AXIS));
        pasarAeliminar.setAlignmentX(CENTER_ALIGNMENT);
        eliminarDatos.setAlignmentX(CENTER_ALIGNMENT);
        botones.add(javax.swing.Box.createVerticalGlue());
        botones.add(pasarAeliminar);
        botones.add(javax.swing.Box.createVerticalStrut(24));
        botones.add(eliminarDatos);
        botones.add(javax.swing.Box.createVerticalGlue());

        javax.swing.JPanel izq = new javax.swing.JPanel(new java.awt.BorderLayout(0, 4));
        izq.add(new javax.swing.JLabel("Datos Originales"), java.awt.BorderLayout.NORTH);
        izq.add(spOrigen, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel der = new javax.swing.JPanel(new java.awt.BorderLayout(0, 4));
        der.add(new javax.swing.JLabel("Datos a Eliminar"), java.awt.BorderLayout.NORTH);
        der.add(spEliminar, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel centro = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        centro.add(izq, java.awt.BorderLayout.WEST);
        centro.add(botones, java.awt.BorderLayout.CENTER);
        centro.add(der, java.awt.BorderLayout.EAST);

        setLayout(new java.awt.BorderLayout());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(centro, java.awt.BorderLayout.WEST);
    }

    /**
     * Relee las tablas de la base. El ResultSet se cierra SIEMPRE: mientras viva
     * mantiene un lock de lectura en SQLite que bloquea el borrado (SQLITE_BUSY).
     */
    private void cargar() throws SQLException, IOException {
        try (ResultSet r = con.cargarTablas()) {
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.setColumnIdentifiers(new Object[]{"No. Tabla", "Codigo"});
            while (r.next()) {
                modelo.addRow(new Object[]{r.getInt("numTabla"), r.getString("codigo")});
            }
            jTablas.setModel(modelo);

            DefaultTableModel mod2 = (DefaultTableModel) jTablasE.getModel();
            mod2.setRowCount(0);
            mod2.setColumnIdentifiers(new Object[]{"No. Tabla", "Codigo"});
        }
    }

    private void pasarAEliminar() {
        TableModel mod1 = jTablas.getModel();
        DefaultTableModel mod2 = (DefaultTableModel) jTablasE.getModel();
        for (int fila : jTablas.getSelectedRows()) {
            mod2.addRow(new Object[]{mod1.getValueAt(fila, 0), mod1.getValueAt(fila, 1)});
        }
    }

    private void eliminar() {
        TableModel mod2 = jTablasE.getModel();
        if (mod2.getRowCount() == 0) {
            return;
        }
        for (int fila = 0; fila < mod2.getRowCount(); fila++) {
            Object v = mod2.getValueAt(fila, 0);
            int numTabla = (v == null ? -1 : (int) v);
            try {
                con.borrarTabla(numTabla);
            } catch (IOException | SQLException ex) {
                Logger.getLogger(RangosTablas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            cargar();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(RangosTablas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
