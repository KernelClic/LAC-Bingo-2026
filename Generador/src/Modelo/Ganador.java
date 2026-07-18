 /* To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author oracle
 */


public class Ganador {

    int numTabla;
    String juego;

    public Ganador() {
    }

    public Ganador(int numTabla, String juego) {
        this.numTabla = numTabla;
        this.juego = juego;
    }

    public void setNumTabla(int numTabla) {
        this.numTabla = numTabla;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }

    public int getNumTabla() {
        return numTabla;
    }

    public String getJuego() {
        return juego;
    }

}
