/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author JSanchez
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MatrizInterfaz extends JFrame {
    private JToggleButton[][] botones;
    private JButton guardarBoton;
    private JButton limpiarPantalla;
    private JButton limpiarBoton;
    private JButton salirBoton;
    private JButton nuevaVentanaBoton;
    private static final int TAMANO = 5;

    public MatrizInterfaz() {
        botones = new JToggleButton[TAMANO][TAMANO];
        JPanel matrizPanel = new JPanel(new GridLayout(TAMANO, TAMANO));
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                botones[i][j] = new JToggleButton("0");
                botones[i][j].addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        JToggleButton button = (JToggleButton) e.getSource();
                        if (button.isSelected()) {
                            button.setText("X");
                        } else {
                            button.setText("0");
                        }
                    }
                });
                matrizPanel.add(botones[i][j]);
            }
        }

        guardarBoton = new JButton("Guardar");
        guardarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // UTF-8 EXPLICITO: con FileWriter se usaba la codificacion del sistema, asi
                // que editar las figuras desde Windows dejaba matriz.txt en windows-1252
                // y los nombres con acento o ñ se corrompian para todos los que lo leen.
                try (PrintWriter writer = new PrintWriter(new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream("/Bingo/db/matriz.txt", true),
                        java.nio.charset.StandardCharsets.UTF_8))) {
                    for (int i = 0; i < TAMANO; i++) {
                        for (int j = 0; j < TAMANO; j++) {
                            if (i==2 && j==2)
                                writer.print("0");
                            else
                                writer.print(botones[i][j].getText());
                            //else
                            //    
                                 
                        }
                        writer.println();
                    }
                    writer.println("-----");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        limpiarBoton = new JButton("Limpiar Archivo");
        limpiarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (PrintWriter writer = new PrintWriter(new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream("/Bingo/db/matriz.txt"),
                        java.nio.charset.StandardCharsets.UTF_8))) {
                    // No escribimos nada, por lo que el archivo se limpia
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        limpiarPantalla = new JButton("Limpiar Pantalla");
        limpiarPantalla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < TAMANO; i++) {
                    for (int j = 0; j < TAMANO; j++) {
                        botones[i][j].setText("0");
                        botones[i][j].setSelected(false);
                    }
                }
            }
        });
        
        
        salirBoton = new JButton("Salir");
        salirBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel botonPanel = new JPanel();
        botonPanel.add(guardarBoton);
        botonPanel.add(limpiarBoton);
        botonPanel.add(limpiarPantalla);
        botonPanel.add(salirBoton);

        add(matrizPanel, BorderLayout.CENTER);
        add(botonPanel, BorderLayout.SOUTH);

        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MatrizInterfaz();
    }
}