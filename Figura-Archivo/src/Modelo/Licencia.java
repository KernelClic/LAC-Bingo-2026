/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;

public class Licencia implements Serializable {

    private int id; // ocupa 4 bytes

    private String fecha; // cada caracter ocupa 2 bytes
    private String nroIP; // cada caracter ocupa 2 bytes
    private String nroIG; // cada caracter ocupa 2 bytes
    private String nroIR; // cada caracter ocupa 2 bytes
    private String nroIC; // cada caracter ocupa 2 bytes
        
    private String RegNroIP; // cada caracter ocupa 2 bytes
    private String RegNroIG; // cada caracter ocupa 2 bytes
    private String RegNroIR; // cada caracter ocupa 2 bytes
    private String RegNroIC; // cada caracter ocupa 2 bytes
        
    
    public Licencia ( int _id, String _fecha, 
                      String _nroIP, String _nroIG, String _nroIR, String _nroIC, 
                      String _RegNroIP,String _RegNroIG,String _RegNroIR, String _RegNroIC){
        
        this.id = _id;
        this.fecha = _fecha;
        this.nroIP = _nroIP;
        this.nroIG = _nroIG;
        this.nroIR = _nroIR;
        this.nroIC = _nroIC;
        this.RegNroIP = _RegNroIP;
        this.RegNroIG = _RegNroIG;
        this.RegNroIR = _RegNroIR;
        this.RegNroIC = _RegNroIC;
        
    }
    
    public Licencia ( ){
        
        this.id = 0;
        this.fecha = "";
        this.nroIP = "";
        this.nroIG = "";
        this.nroIR = "";
        this.nroIC = "";
        this.RegNroIP = "";
        this.RegNroIG = "";
        this.RegNroIR = "";
        this.RegNroIC = "";
        
    }
    
    public int getTamaño() {
        return  getFecha().length()*2+
                getNroIP().length()*2+
                getNroIG().length()*2+
                getNroIR().length()*2+
                getNroIC().length()*2+
                getRegNroIP().length()*2+
                getRegNroIG().length()*2+
                getRegNroIR().length()*2+                
                getRegNroIC().length()*2+                
                (2);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int _id) {
        this.id = _id;
    }    
    
    
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the nroIP
     */
    public String getNroIP() {
        return nroIP;
    }

    /**
     * @param nroIP the nroIP to set
     */
    public void setNroIP(String nroIP) {
        this.nroIP = nroIP;
    }

    /**
     * @return the nroIG
     */
    public String getNroIG() {
        return nroIG;
    }

    /**
     * @param nroIG the nroIG to set
     */
    public void setNroIG(String nroIG) {
        this.nroIG = nroIG;
    }

    /**
     * @return the nroIR
     */
    public String getNroIR() {
        return nroIR;
    }

    /**
     * @param nroIR the nroIR to set
     */
    public void setNroIR(String nroIR) {
        this.nroIR = nroIR;
    }

    /**
     * @return the nroIC
     */
    public String getNroIC() {
        return nroIC;
    }

    /**
     * @param nroIC the nroIC to set
     */
    public void setNroIC(String nroIC) {
        this.nroIC = nroIC;
    }

    /**
     * @return the RegNroIP
     */
    public String getRegNroIP() {
        return RegNroIP;
    }

    /**
     * @param RegNroIP the RegNroIP to set
     */
    public void setRegNroIP(String RegNroIP) {
        this.RegNroIP = RegNroIP;
    }

    /**
     * @return the RegNroIG
     */
    public String getRegNroIG() {
        return RegNroIG;
    }

    /**
     * @param RegNroIG the RegNroIG to set
     */
    public void setRegNroIG(String RegNroIG) {
        this.RegNroIG = RegNroIG;
    }

    /**
     * @return the RegNroIR
     */
    public String getRegNroIR() {
        return RegNroIR;
    }

    /**
     * @param RegNroIR the RegNroIR to set
     */
    public void setRegNroIR(String RegNroIR) {
        this.RegNroIR = RegNroIR;
    }

    /**
     * @return the RegNroIC
     */
    public String getRegNroIC() {
        return RegNroIC;
    }

    /**
     * @param RegNroIC the RegNroIC to set
     */
    public void setRegNroIC(String RegNroIC) {
        this.RegNroIC = RegNroIC;
    }

    
}