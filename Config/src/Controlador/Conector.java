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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oracle
 */
public final class Conector {

    String url;

    public void setUrl(String url) {
        this.url = url;
    }
    Connection connect;

    public Conector() throws IOException {
        this.setUrl(AccessFile.getRutaFileDB() + "tablas.db");
        if (!AccessFile.buscarFile(new File(AccessFile.getRutaFileDB() + "tablas.db")) || !AccessFile.buscarFile(new File(AccessFile.getRutaFiledb()))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }

    }

    public void connect() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (connect != null) {
                //System.out.println("Conectado");
                // CREAR Sentencia 
                Statement enunciado;
                enunciado = connect.createStatement();

            }
        } catch (SQLException ex) {
            System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
        }
    }

    public void connectConsulta() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + url);
            if (connect != null) {
                // CREAR Sentencia 
                Statement enunciado;
                enunciado = connect.createStatement();
            }
        } catch (SQLException ex) {
            System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
        }
    }

    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ResultSet cargarTablas() throws SQLException, IOException {
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        return (result);
    }

    public void borrarTabla(int ntabla) throws IOException, SQLException {
        if (ntabla == -1) {
            return;
        }
        Boolean Login = false;

        PreparedStatement st = connect.prepareStatement("delete from Tablas where numTabla = trim(?)");
        st.setInt(1, ntabla);
        st.execute();

    }

    
    

}
