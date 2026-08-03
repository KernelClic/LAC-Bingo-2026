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
    
    /**
     * Numero de tabla con 4 digitos, rellenando con ceros a la izquierda:
     * 3 -> "0003", 43 -> "0043", 368 -> "0368", 8978 -> "8978". Si tuviera mas
     * de 4 digitos se muestra completo, y si no fuera numerico se deja tal cual.
     */
    public String getCodigoFormateado() {
        String c = codigo == null ? "" : codigo.trim();
        try {
            return String.format("%04d", Long.parseLong(c));
        } catch (NumberFormatException ex) {
            return c;
        }
    }

    public String getCodigo (){
        return this.codigo;
    }

}
