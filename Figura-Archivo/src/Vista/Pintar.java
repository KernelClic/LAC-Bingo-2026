package Vista;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class Pintar {
    private static final int TAMANO = 5;
    JPanel matrizPanel;
    private JToggleButton[][] botones;



    public Pintar() {
        botones = new JToggleButton[TAMANO][TAMANO];
        matrizPanel = new JPanel( new GridLayout(5, 25));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 25; j++) {
                botones[i][j].add(new JToggleButton("0"));  
                botones[i][j].setBackground(java.awt.Color.RED);
               
            }
        }
    }

    

    
}
