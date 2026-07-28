/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Tabla;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oracle
 */
public final class Bingo {

    String url;
    String app;
    String codigo;
    
    private String patronBusqueda = "9f0AwB7J8CpDuÑd5E6FQGlxnHMbcI3ñK4LeUNzO1ms2PtRvSVkWXqirTYaghZjoy";
    private String patronEncripta = "wxBU7nIGj9Flm8f0ñAH1bcK3hdi4WJ5ZLCpDeMvTQuVkXqraYE6gosyNzÑOP2RSt";
    

    Connection connect;
    
    public String getUrl() {
        return url;
    }

    public String getApp() {
        return app;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String encriptarCaracter(String caracter, int variable, int indice){
      int ind;
      if(patronBusqueda.indexOf(caracter)!=-1){
        ind = (patronBusqueda.indexOf(caracter) + variable + indice) % patronBusqueda.length();          
        return patronEncripta.substring(ind, ind+1);
      }
     return caracter;
    }    
    
    public String encriptarCadena(String cadena){
    String resultado="";
    for(int pos = 0; pos< cadena.length(); pos++){
      if(pos==0){
        resultado= encriptarCaracter(cadena.substring(pos,pos+1),cadena.length(),pos);    
      }
      else{
        resultado+= encriptarCaracter(cadena.substring(pos,pos+1),cadena.length(),pos);    
      }
        }
     return resultado;
     }
    
    public String desencriptaCadena(String cadena){
       String original="";
       for(int pos = 0; pos< cadena.length(); pos++){
         if(pos==0){
            original=desencriptaCaracter(cadena.substring(pos,pos+1),cadena.length(),pos);
          }
          else{
            original+=desencriptaCaracter(cadena.substring(pos,pos+1),cadena.length(),pos);
           }
       }
            return original;
    }
    
    public String desencriptaCaracter(String caracter, int variable, int indice){
 int ind=0;
  if(patronEncripta.indexOf(caracter) != -1){
    if((patronEncripta.indexOf(caracter) - variable - indice) > 0){
       ind = (patronEncripta.indexOf(caracter) - variable - indice) % patronEncripta.length(); 
    }
    else{
       ind = (patronBusqueda.length()) + ((patronEncripta.indexOf(caracter)- variable - indice) % patronEncripta.length());
    }
    ind = ind % patronEncripta.length();
   return patronBusqueda.substring(ind, ind + 1);
  }
  else
  {
   return caracter;
  }         
}
    
    
     public Bingo() throws IOException {
        this.setUrl(AccesoAleatorio.getRutaFileDB() + "tablas.db");
        if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFileDB() + "tablas.db"))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }

    }

    public void connect() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (connect != null) {
             /*   
                Statement enunciado;
                enunciado = connect.createStatement();

                // CREAR UNA TABLA NUEVA, LA BORRA SI EXISTE
                enunciado.execute("DROP TABLE IF EXISTS Bingo;");
                enunciado.execute("CREATE TABLE Bingo (app text, codigo text);");
            */
             
             /*
             PreparedStatement 
                        st0 = connect.prepareStatement("delete from Bingo");
            st0.execute();
             
            
                PreparedStatement 
                        st = connect.prepareStatement("insert into Bingo (app,codigo)"
                                                    + " values (?,?)");
            st.setString(1, encriptarCadena("Generador"));
            st.setString(2, encriptarCadena("Generador"));
            st.execute();
                 
            PreparedStatement 
                        st1 = connect.prepareStatement("insert into Bingo (app,codigo)"
                                                    + " values (?,?)");
            st1.setString(1, encriptarCadena("Pantalla"));
            st1.setString(2, encriptarCadena("Pantalla"));
            st1.execute();
            */
            }
        } catch (SQLException ex) {
            System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
        }
    }


    public void borrarBase() throws SQLException {
        Statement enunciado;
        enunciado = connect.createStatement();
        enunciado.execute("delete from Bingo");
    }

    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

public Boolean autenticar (String app, String pass ) throws SQLException, IOException {
        
    
        Boolean Login=false;
        String appCifrado = encriptarCadena(app);
        String passCifrado = encriptarCadena(pass);
        
        String sql = "SELECT app, codigo FROM Bingo WHERE trim(app) = trim(?) and trim(codigo) = trim(?);";
        
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement(sql);
        
        st.setString(1, appCifrado);
        st.setString(2, passCifrado);
        
        result = st.executeQuery();
        
        while (result.next()) {
            if (result.getString("app").equals(appCifrado) ) 
                if (result.getString("codigo").equals(passCifrado) ) 
                    Login=true;
        }
        
        return Login;
        
    }    
    
   public void actualizarPassword (String app, String pass, String newpass ) throws SQLException, IOException {
        Boolean Login=false;
        
        if (autenticar(app,pass)) {
            PreparedStatement st = connect.prepareStatement("delete from Bingo where app = trim(?)");
            st.setString(1, encriptarCadena(app));
            st.execute();
            
                PreparedStatement 
                        st0 = connect.prepareStatement("insert into Bingo (app,codigo)"
                                                    + " values (?,?)");
            st0.setString(1, encriptarCadena(app));
            st0.setString(2, encriptarCadena(newpass));
            st0.execute();
            
        }
        
        
   }

}
