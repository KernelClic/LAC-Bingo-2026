/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;

public class Configuracion implements Serializable {
    private int id; // ocupa 4 bytes
    private int intento; // ocupa 4 bytes
    private String juego; // cada caracter ocupa 2 bytes
    private int tab1; // ocupa 4 bytes
    private String tabla1; // cada caracter ocupa 2 bytes
    private int tab2; // ocupa 4 bytes
    private String tabla2; // cada caracter ocupa 2 bytes
    private int tab3; // ocupa 4 bytes
    private String tabla3; // cada caracter ocupa 2 bytes
  
    public Configuracion(int id, int intento, String juego, String tabla1, String tabla2, String tabla3) {
        this.id = id;
        this.intento = intento;
        this.juego = juego;
        this.tabla1 = tabla1;
        this.tabla2 = tabla2;
        this.tabla3 = tabla3;
    } 

    public Configuracion(int id, int intento, String juego, int tab1, String tabla1, int tab2, String tabla2, int tab3, String tabla3) {
        this.id = id;
        this.intento = intento;
        this.juego = juego;
        this.tab1 = tab1;
        this.tabla1 = tabla1;
        this.tab2 = tab2;
        this.tabla2 = tabla2;
        this.tab3 = tab3;
        this.tabla3 = tabla3;
    }
        
    public Configuracion() {
        id = 0; 
        intento = 0; 
        juego = "N/A";
        tabla1 = "N/A";
        tabla2 = "N/A";
        tabla3 = "N/A";
        
        tab1=0;
        tab2=0;
        tab3=0;
        
    }

    @Override
    public String toString() {
        return "\nId        : "  + id +
               "\nIntentos  : "  + intento + 
               "\nMensaje   : "  + juego +
               "\nTabla1    : "  + tabla1 + 
               "\nTabla2    : "  + tabla2 + 
               "\nTabla4    : "  + tabla3 ;
    }   
    
    public int getTamaño() {
        return  getJuego().length() *2+
                getTabla1().length()*2+
                getTabla2().length()*2+
                getTabla3().length()*2+
                (2+2+2+2+2);
    }

    public int getTab1() {
        return tab1;
    }

    public void setTab1(int tab1) {
        this.tab1 = tab1;
    }

    public int getTab2() {
        return tab2;
    }

    public void setTab2(int tab2) {
        this.tab2 = tab2;
    }

    public int getTab3() {
        return tab3;
    }

    public void setTab3(int tab3) {
        this.tab3 = tab3;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIntento() {
        return intento;
    }

    public void setIntento(int intento) {
        this.intento = intento;
    }

    public String getJuego() {
        return juego;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public String getTabla1() {
        return tabla1;
    }

    public void setTabla1(String tabla1) {
        this.tabla1 = tabla1;
    }

    public String getTabla2() {
        return tabla2;
    }

    public void setTabla2(String tabla2) {
        this.tabla2 = tabla2;
    }

    public String getTabla3() {
        return tabla3;
    }

    public void setTabla3(String tabla3) {
        this.tabla3 = tabla3;
    }
}