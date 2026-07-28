/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

//import Controlador.AccessFile;
import Controlador.AccessFile;
import Controlador.Conector;
import Modelo.Configuracion;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author oracle
 */
public class Config01 extends javax.swing.JFrame {

    /**
     * Creates new form Config
     */
    static final int MAXTABLAS = 10000;
    static final int MAXEXCEPCIONES = 25;
    static final int MAXBINGO = 25;
    static final int MAXWTABLA = 4;
    static final int MAXVECTOR = 5;


    private int MaxTablas;
    private int nObligatorios;
    private int codigoTablas = 1;
    private int[] excNum;
    private int[] wTabla;
    private int[] vector;
    private int[] bingo;
    private int[] bingo_w;

    private int[] tmpWtabla;

    private int[][] Tablas;

    private Conector con;

    public Config01() throws IOException, SQLException {
        initComponents();
        reorganizarPanelConfiguracion();
        this.setLocationRelativeTo(null);
        jTablasE.removeAll();
        jTablas.removeAll();
        configuracionInicial();
        rangoTablas();

    }

    /**
     * Rearma el panel "Configuracion Inicial" con una rejilla limpia de tres
     * filas (No Intentos / Mensaje / Tablas Ganadoras) y los botones alineados
     * a la derecha, todos del mismo ancho y con su altura natural.
     *
     * El layout original venia del editor de formularios y tenia dos defectos:
     * el boton "Generar" se estiraba a toda la altura disponible
     * (DEFAULT_SIZE, 35, Short.MAX_VALUE) y al final habia un hueco fijo de 276
     * px que agrandaba la ventana sin motivo. Se rehace aqui, despues de
     * initComponents(), para no tocar el bloque generado.
     */
    private void reorganizarPanelConfiguracion() {
        // "Cambiar Contraseña" no aplica en la version Universal; se retira del
        // panel para que no ocupe lugar en la rejilla.
        jPanel3.remove(btn_updPassword);

        final int ANCHO_BOTON = 160;

        javax.swing.GroupLayout dis = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(dis);
        dis.setAutoCreateGaps(true);
        dis.setAutoCreateContainerGaps(true);

        dis.setHorizontalGroup(
            dis.createSequentialGroup()
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18)
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIntentos, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 267,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dis.createSequentialGroup()
                        .addComponent(txtTabla01, javax.swing.GroupLayout.PREFERRED_SIZE, 79,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTabla02, javax.swing.GroupLayout.PREFERRED_SIZE, 79,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTabla03, javax.swing.GroupLayout.PREFERRED_SIZE, 85,
                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18)
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, ANCHO_BOTON,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar1, javax.swing.GroupLayout.PREFERRED_SIZE, ANCHO_BOTON,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_SalirGenerador, javax.swing.GroupLayout.PREFERRED_SIZE, ANCHO_BOTON,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dis.setVerticalGroup(
            dis.createSequentialGroup()
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtIntentos, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dis.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTabla01, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTabla02, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTabla03, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_SalirGenerador))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    private void rangoTablas() throws SQLException, IOException {
        //Tabla t = new Tabla();
        ResultSet r = null;
        r = con.cargarTablas();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"No. Tabla", "Codigo"});

        DefaultTableModel mod2 = (DefaultTableModel) jTablasE.getModel();
        mod2.setColumnIdentifiers(new Object[]{"No. Tabla", "Codigo"});

        while (r.next()) {
            /*t.setNumTabla(r.getInt("numTabla"));
                t.setCodigo(r.getString("codigo"));
             */
            modelo.addRow(new Object[]{r.getInt("numTabla"), r.getString("codigo")});
            jTablas.setModel(modelo);

        }
        jTablasE.removeAll();

    }

    private void configuracionInicial() throws IOException {
        int i, j;
        con = new Conector();
        con.connect();
        MaxTablas = 0;
        excNum = new int[MAXEXCEPCIONES];
        for (i = 0; i < MAXEXCEPCIONES; i++) {
            excNum[i] = 0;
        }
        wTabla = new int[MAXWTABLA];
        for (i = 0; i < MAXWTABLA; i++) {
            wTabla[i] = 0;
        }
        vector = new int[MAXVECTOR];
        for (i = 0; i < MAXVECTOR; i++) {
            vector[i] = 0;
        }
        bingo = new int[MAXBINGO];
        for (i = 0; i < MAXBINGO; i++) {
            bingo[i] = 0;
        }
        bingo_w = new int[MAXBINGO];
        for (i = 0; i < MAXBINGO; i++) {
            bingo_w[i] = 0;
        }
        if (!AccessFile.buscarFile(new File(AccessFile.getRutaFileDB() + "tablas.db"))
                // ||!AccessFile.validarLic(new File(AccessFile.getRutaFileDb()),"C")
                ) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Juego = new javax.swing.ButtonGroup();
        BingoCodificacion = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnGenerar = new javax.swing.JButton();
        btn_updPassword = new javax.swing.JButton();
        btn_updPassword.setVisible(false); // password change removed en Universal
        btn_SalirGenerador = new javax.swing.JButton();
        txtIntentos = new javax.swing.JTextField();
        txtMensaje = new javax.swing.JTextField();
        txtTabla01 = new javax.swing.JTextField();
        txtTabla02 = new javax.swing.JTextField();
        txtTabla03 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnGenerar1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablas = new javax.swing.JTable();
        pasarAeliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablasE = new javax.swing.JTable();
        eliminarDatos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Configurar");
        setName("Configurar"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnGenerar.setText("Generar");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        btn_updPassword.setText("Cambiar Contraseña");
        btn_updPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updPasswordActionPerformed(evt);
            }
        });

        btn_SalirGenerador.setText("Salir");
        btn_SalirGenerador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SalirGeneradorActionPerformed(evt);
            }
        });

        txtIntentos.setText("0");

        txtMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMensajeActionPerformed(evt);
            }
        });

        txtTabla01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTabla01ActionPerformed(evt);
            }
        });

        txtTabla02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTabla02ActionPerformed(evt);
            }
        });

        txtTabla03.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTabla03ActionPerformed(evt);
            }
        });

        jLabel5.setText("No Intentos");

        jLabel6.setText("Mensaje");

        jLabel7.setText("Tablas Ganadoras");

        btnGenerar1.setText("Cargar ");
        btnGenerar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtIntentos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(247, 247, 247)
                        .addComponent(btnGenerar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGenerar1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtTabla01, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTabla02, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTabla03, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_SalirGenerador, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_updPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIntentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(14, 14, 14)
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnGenerar, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGenerar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTabla01, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtTabla03, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtTabla02, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_updPassword)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_SalirGenerador)
                        .addGap(276, 276, 276))))
        );

        jTabbedPane1.addTab("Configuracion Inicial", jPanel3);

        jTablas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTablas);

        pasarAeliminar.setText("Pasar");
        pasarAeliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasarAeliminarActionPerformed(evt);
            }
        });

        jLabel1.setText("Datos Originales");

        jLabel2.setText("Datos a Eliminar");

        jTablasE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTablasE);

        eliminarDatos.setText("Eliminar");
        eliminarDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarDatosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pasarAeliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addComponent(eliminarDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(240, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(93, 93, 93)
                                .addComponent(pasarAeliminar)
                                .addGap(93, 93, 93)
                                .addComponent(eliminarDatos)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Rangos de Tablas", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

        con.close();
    }//GEN-LAST:event_formWindowClosed

    private void btnGenerar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerar1ActionPerformed
        try {
            // TODO add your handling code here:
            AccessFile.cargarPartida();

            // Sin partida guardada no hay nada que cargar: avisar en vez de
            // reventar con NullPointerException al leer un registro ausente.
            if (AccessFile.getNumeroRegistros() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay una partida guardada todavia.\n"
                        + "Configure los valores y presione Generar para crearla.",
                        "Sin configuracion", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Configuracion conf1 = AccessFile.getConf(AccessFile.buscarRegistro(1));
            txtIntentos.setText(Integer.toString(conf1.getIntento()));
            txtMensaje.setText(conf1.getJuego());
            txtTabla01.setText(conf1.getTabla1());
            txtTabla02.setText(conf1.getTabla2());
            txtTabla03.setText(conf1.getTabla3());
            
/*                       
            Configuracion conf2 = AccessFile.getConf(AccessFile.buscarRegistro(2));
            Configuracion conf3 = AccessFile.getConf(AccessFile.buscarRegistro(3));
            Configuracion conf4 = AccessFile.getConf(AccessFile.buscarRegistro(4));
            Configuracion conf5= AccessFile.getConf(AccessFile.buscarRegistro(5));           
            Configuracion conf6= AccessFile.getConf(AccessFile.buscarRegistro(6));
            Configuracion conf7= AccessFile.getConf(AccessFile.buscarRegistro(7));
            Configuracion conf8 = AccessFile.getConf(AccessFile.buscarRegistro(8));          
            Configuracion conf9 = AccessFile.getConf(AccessFile.buscarRegistro(9));
            Configuracion conf10 = AccessFile.getConf(AccessFile.buscarRegistro(10));
            Configuracion conf11 = AccessFile.getConf(AccessFile.buscarRegistro(11));
            Configuracion conf12= AccessFile.getConf(AccessFile.buscarRegistro(12));           
            Configuracion conf13= AccessFile.getConf(AccessFile.buscarRegistro(13));

  */          
            /***/
            
            
            
            /***/
            
            
            
            
            AccessFile.cerrar();
        } catch (IOException ex) {
            Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGenerar1ActionPerformed

    private void txtTabla03ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTabla03ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTabla03ActionPerformed

    private void txtTabla02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTabla02ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTabla02ActionPerformed

    private void txtTabla01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTabla01ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTabla01ActionPerformed

    private void txtMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMensajeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMensajeActionPerformed

    private void btn_SalirGeneradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SalirGeneradorActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btn_SalirGeneradorActionPerformed

    private void btn_updPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updPasswordActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_updPasswordActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        String _intento = txtIntentos.getText();

        if (_intento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "¡Debe ingresar un número entero mayor o igual a cero (0)!", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int intento;
        try {
            intento = Integer.parseInt(_intento);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "¡Debe ingresar un número entero mayor o igual a cero (0)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mensaje = txtMensaje.getText().trim();
        String tabla1 = txtTabla01.getText().trim().isEmpty() ? "-1" : txtTabla01.getText().trim();
        String tabla2 = txtTabla02.getText().trim().isEmpty() ? "-1" : txtTabla02.getText().trim();
        String tabla3 = txtTabla03.getText().trim().isEmpty() ? "-1" : txtTabla03.getText().trim();

        if (intento > 0) {
            if (mensaje.isEmpty()) {
                JOptionPane.showMessageDialog(this, "¡Debe ingresar un Mensaje!", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tabla1.isEmpty() && tabla2.isEmpty() && tabla3.isEmpty()) {
                JOptionPane.showMessageDialog(this, "¡Debe ingresar a menos una Tabla!", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

        }

        try {
            AccessFile.nuevaPartida();
            AccessFile.añadirConf(new Configuracion(1, intento, mensaje, tabla1, tabla2, tabla3));
            AccessFile.añadirConf(new Configuracion(2, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(3, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(4, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(5, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(6, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(7, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(8, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(9, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(10, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(11, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(12, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(13, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(14, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(15, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(16, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(17, 0, "N/A", "N/A", "N/A", "N/A"));
            AccessFile.añadirConf(new Configuracion(18, 0, "N/A", "N/A", "N/A", "N/A"));
          

            AccessFile.cerrar();
            JOptionPane.showMessageDialog(this, "El registro se realizó correctamente.", "Notificación", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en la escritura de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerarActionPerformed

    private void eliminarDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarDatosActionPerformed

        if (jTablas.getSelectedRow() == -1) {
            return;
        }

        //Eliminar las filas Seleccionadas
        TableModel mod2 = jTablasE.getModel();
        jTablasE.selectAll();
        int[] filas = jTablasE.getSelectedRows();
        Object[] row = new Object[2];

        for (int i = 0; i < filas.length; i++) {
            row[0] = mod2.getValueAt(filas[i], 0); //NumTabla
            int numTabla = (row[0] == null ? -1 : (int) row[0]);

            try {
                con.borrarTabla(numTabla);

                // LLamar eliminar de la base de datos
                // mod2.addRow(row);
            } catch (IOException ex) {
                Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        jTablasE.removeAll();
        jTablas.removeAll();

        try {
            rangoTablas();
        } catch (SQLException ex) {
            Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_eliminarDatosActionPerformed

    private void pasarAeliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasarAeliminarActionPerformed
        // TODO add your handling code here:

        TableModel mod1 = jTablas.getModel();
        int[] filas = jTablas.getSelectedRows();
        Object[] row = new Object[2];

        DefaultTableModel mod2 = (DefaultTableModel) jTablasE.getModel();

        for (int i = 0; i < filas.length; i++) {
            row[0] = mod1.getValueAt(filas[i], 0);
            row[1] = mod1.getValueAt(filas[i], 1);
            mod2.addRow(row);
        }

    }//GEN-LAST:event_pasarAeliminarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup BingoCodificacion;
    private javax.swing.ButtonGroup Juego;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnGenerar1;
    private javax.swing.JButton btn_SalirGenerador;
    private javax.swing.JButton btn_updPassword;
    private javax.swing.JButton eliminarDatos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTablas;
    private javax.swing.JTable jTablasE;
    private javax.swing.JButton pasarAeliminar;
    private javax.swing.JTextField txtIntentos;
    private javax.swing.JTextField txtMensaje;
    private javax.swing.JTextField txtTabla01;
    private javax.swing.JTextField txtTabla02;
    private javax.swing.JTextField txtTabla03;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Config01.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Config01.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Config01.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Config01.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //con = new Conector();
        //</editor-fold>
        //</editor-fold>
        //con = new Conector();

        // Create and display the form 
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Config01().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Config01.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

}
