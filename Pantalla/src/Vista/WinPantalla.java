/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Controlador.TraductorMensajes;
import Modelo.Ganador;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.Timer;



/**
 *
 * @author oracle
 */
public class WinPantalla extends javax.swing.JDialog {
    private Timer timer; 
    
    
    
    /**
     * Creates new form WinPantalla
     */
    public WinPantalla(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
    }
    public WinPantalla(java.awt.Frame parent, boolean modal, String mensaje, Vector <Ganador> vWin, int seg) {
        super(parent, modal);
        initComponents();
        Timer timer = new Timer (seg*3*1000, (ActionEvent e) -> {
            cerrar(); 
        }); 
        msg.setText("<html>");
        for (int i=0; i<vWin.size();i++){
            
            //msg.setText(msg.getText()+" "+"<br>"+vWin.get(i).getJuego()+" Tabla No. "+generarCodigo(vWin.get(i).getNumTabla()));
            msg.setText(msg.getText()+" "+"<br>"+TraductorMensajes.traducir(vWin.get(i).getJuego())+" Tabla No. "+vWin.get(i).getCodigoFormateado());
            //msg.setText(msg.getText()+" "+"<br>"+vWin.get(i).getJuego()+" Tabla No. "+Integer.toString(vWin.get(i).getNumTabla()));
        }
        msg.setText(msg.getText()+"</html>");
        timer.start();
    }
    
        public WinPantalla(java.awt.Frame parent, boolean modal, Vector <Ganador> vWin, int seg) {
        super(parent, modal);
        initComponents();
        construir(vWin, seg, false);
    }

    /**
     * Variante con boton "Cerrar" visible (para el boton "Mostrar Ganadores").
     * La ventana permanece habilitada {@code seg} segundos y luego se cierra
     * sola; el usuario puede cerrarla antes con el boton.
     */
    public WinPantalla(java.awt.Frame parent, boolean modal, Vector <Ganador> vWin, int seg, boolean conCerrar) {
        super(parent, modal);
        initComponents();
        construir(vWin, seg, conCerrar);
    }

    private void construir(Vector <Ganador> vWin, int seg, boolean conCerrar) {
        timer = new Timer (seg*1000, (ActionEvent e) -> {
            cerrar();
        });
        timer.setRepeats(false);
        msg.setFont(new Font("Dialog", Font.BOLD, 40));
        msg.setHorizontalAlignment(JLabel.CENTER);
        msg.setVerticalAlignment(JLabel.TOP);

        // Agrupa TODOS los ganadores por figura (juego), preservando el orden de
        // aparicion; lista todas las tablas que ganaron cada figura.
        java.util.LinkedHashMap<String, java.util.List<String>> grupos = new java.util.LinkedHashMap<>();
        for (int i = 0; i < vWin.size(); i++) {
            String fig = TraductorMensajes.traducir(vWin.get(i).getJuego());
            grupos.computeIfAbsent(fig, k -> new java.util.ArrayList<String>()).add(vWin.get(i).getCodigoFormateado());
        }
        java.util.List<String> claves = new java.util.ArrayList<>(grupos.keySet());
        final String VERDE = "#00E000";     // ganador ACTUAL (verde intenso)
        final String AMARILLO = "#FFD633";  // ganadores anteriores
        StringBuilder sb = new StringBuilder("<html><div style='text-align:center'>");
        // El ganador ACTUAL (ultima figura) va PRIMERO; los anteriores bajan.
        for (int k = claves.size() - 1; k >= 0; k--) {
            String fig = claves.get(k);
            boolean actual = (k == claves.size() - 1);
            String col = actual ? VERDE : AMARILLO;
            sb.append("<div style='margin-bottom:16px'>");
            sb.append("<span style='font-size:32pt; color:").append(col).append("'><b>")
              .append(fig).append("</b></span><br>");
            for (String cod : grupos.get(fig)) {
                sb.append("<span style='font-size:54pt; color:").append(col).append("'><b>Tabla No. ")
                  .append(cod).append("</b></span><br>");
            }
            sb.append("</div>");
        }
        sb.append("</div></html>");
        msg.setText(sb.toString());

        // El formulario fijaba el texto en 768x800 con GroupLayout, asi que la
        // ventana media siempre lo mismo hubiera un ganador o veinte. Se pasa a
        // BorderLayout para que el panel tome la medida real del contenido.
        jPanel1.removeAll();
        jPanel1.setLayout(new java.awt.BorderLayout());
        msg.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 24, 32));
        jPanel1.add(msg, java.awt.BorderLayout.CENTER);
        jPanel1.setPreferredSize(null);

        // Envolver en scroll para poder ver TODOS cuando hay muchos ganadores.
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(jPanel1,
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(28);
        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(sp, java.awt.BorderLayout.CENTER);

        // Boton "Cerrar" (solo cuando se abre manualmente con "Mostrar Ganadores").
        if (conCerrar) {
            javax.swing.JButton bCerrar = new javax.swing.JButton("Cerrar");
            bCerrar.setFont(new Font("Dialog", Font.BOLD, 22));
            bCerrar.setFocusPainted(false);
            bCerrar.setBackground(new java.awt.Color(200, 40, 40));
            bCerrar.setForeground(java.awt.Color.WHITE);
            bCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            bCerrar.addActionListener((ActionEvent e) -> cerrar());
            javax.swing.JPanel barra = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 10));
            barra.setBackground(new java.awt.Color(0, 0, 0));
            barra.add(bCerrar);
            getContentPane().add(barra, java.awt.BorderLayout.SOUTH);
        }

        // Tamaño segun el contenido. El pack() de initComponents corre antes de
        // que exista el texto, asi que la ventana quedaba con la medida fija del
        // formulario: enorme con un solo ganador y con scroll cuando eran varios.
        ajustarAlContenido(sp, conCerrar);

        // Fade-in al aparecer (transicion suave desde el flash de ganador).
        try {
            setOpacity(0f);
            final float[] op = {0f};
            Timer fin = new Timer(20, null);
            fin.addActionListener(ev -> {
                op[0] += 0.10f;
                if (op[0] >= 1f) { op[0] = 1f; fin.stop(); }
                try { setOpacity(op[0]); } catch (Throwable ig) { }
            });
            fin.setRepeats(true);
            fin.start();
        } catch (Throwable ig) { }

        timer.start();
    }
    

    /**
     * Ajusta la ventana a lo que realmente se va a mostrar, sin pasarse de la
     * pantalla: si el contenido es mas alto que el area disponible se recorta al
     * maximo y aparece el scroll. Queda centrada.
     */
    private void ajustarAlContenido(javax.swing.JScrollPane sp, boolean conCerrar) {
        // Monitor donde esta la ventana de juego: con dos pantallas hay que
        // centrar en esa, no en el escritorio completo ni en la principal.
        java.awt.GraphicsConfiguration gc = (getParent() != null && getParent().getGraphicsConfiguration() != null)
                ? getParent().getGraphicsConfiguration()
                : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
        java.awt.Rectangle pantalla = gc.getBounds();
        java.awt.Insets bordes = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(gc);
        java.awt.Rectangle libre = new java.awt.Rectangle(
                pantalla.x + bordes.left,
                pantalla.y + bordes.top,
                pantalla.width - bordes.left - bordes.right,
                pantalla.height - bordes.top - bordes.bottom);
        int maxAncho = (int) (libre.width * 0.90);
        int maxAlto = (int) (libre.height * 0.90);

        // La medida sale del texto ya renderizado, no del formulario.
        java.awt.Dimension pref = msg.getPreferredSize();
        int ancho = Math.min(Math.max(pref.width + 40, 420), maxAncho);
        int alto = pref.height + 40 + (conCerrar ? 70 : 0);
        alto = Math.min(Math.max(alto, 200), maxAlto);

        setSize(ancho, alto);
        // Centrada en ese monitor. setLocationRelativeTo(padre) dejaba la
        // ventana descolocada cuando el padre no estaba centrado o el juego
        // corria en el segundo monitor.
        setLocation(libre.x + (libre.width - ancho) / 2,
                    libre.y + (libre.height - alto) / 2);
        sp.getVerticalScrollBar().setValue(0);
    }

    public String generarCodigo (int nTabla) {
        
        Integer nT = nTabla;
        String original = nT.toString();
        String ret = original; 
        

                if (ret.length() > 3){ 
                String mil = original.substring(0, original.length()-3 );
                
                System.out.println("Codigo: "+mil);
                
                mil = mil.replace('0', 'W');
                mil = mil.replace('1', 'R');
                mil = mil.replace('2', 'T');
                mil = mil.replace('3', 'P');
                mil = mil.replace('4', 'X');
                mil = mil.replace('5', 'Z');
                mil = mil.replace('6', 'A');
                mil = mil.replace('7', 'K');
                mil = mil.replace('8', 'E');
                mil = mil.replace('9', 'H');
                
                //System.out.println("Codigo: "+mil+original.substring(original.length()-2,original.length() ));
                ret = mil+original.substring(original.length()-3,original.length() );
 
        }
        
        return ret;
    }    
        /*
    public String generarCodigo (int nTabla) {
        
        Integer nT = nTabla;
        String original = nT.toString();
        String ret = original; 
        
        ret = ret.replace('0', 'W');
        ret = ret.replace('1', 'R');
        ret = ret.replace('2', 'T');
        ret = ret.replace('3', 'P');
        ret = ret.replace('4', 'X');
        ret = ret.replace('5', 'Z');
        ret = ret.replace('6', 'A');
        ret = ret.replace('7', 'K');
        ret = ret.replace('8', 'E');
        ret = ret.replace('9', 'H');
        
        return ret;
    }
        
    */
    
    
    public void cerrar (){
        if (timer != null) timer.stop();
        this.dispose();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        msg = new javax.swing.JLabel();

        setTitle("Ganadores");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(0, 0, 0));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setModal(true);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        msg.setFont(new java.awt.Font("Dialog", 1, 40)); // NOI18N
        msg.setForeground(new java.awt.Color(51, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 32, Short.MAX_VALUE)
                .addComponent(msg, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(msg, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 13, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel msg;
    // End of variables declaration//GEN-END:variables
}
