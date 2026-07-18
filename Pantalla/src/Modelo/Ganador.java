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
    String codigo;

    public Ganador() {
    }

    public Ganador(int numTabla, String juego) {
        this.numTabla = numTabla;
        this.juego = juego;
    }

    public Ganador(int numTabla, String juego, String codigo) {
        this.numTabla = numTabla;
        this.juego = juego;
        this.codigo = codigo;
    }

    public void setNumTabla(int numTabla) {
        this.numTabla = numTabla;
    }

    public void setJuego(String juego) {
        this.juego = juego;
    }
    
    public void setCodigo(String codigo){
        this.codigo=codigo;
    }

    public int getNumTabla() {
        return numTabla;
    }

    public String getJuego() {
        return juego;
    }
    
    public String getCodigo (){
        return this.codigo;
    }

}
