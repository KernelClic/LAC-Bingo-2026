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
        if (!AccessFile.buscarFile(new File(AccessFile.getRutaFileDB() + "tablas.db"))) {
            System.out.print("Error de Conexion a Base de Datos");
            System.exit(0);
        }

    }

    /**
     * Conexion UNICA del proceso. En la ventana unificada conviven Config01,
     * Config02 y Config03, y antes cada una abria la suya: SQLite solo admite
     * un escritor, asi que el lock de lectura de una impedia el DELETE de otra
     * y saltaba SQLITE_BUSY ("database is locked"). Compartiendo la conexion el
     * conflicto desaparece.
     */
    private void abrirCompartida() throws SQLException {
        connect = ConexionBD.get(url);
    }

    public void connect() {
        try {
            abrirCompartida();
        } catch (SQLException ex) {
            System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
        }
    }

    public void connectConsulta() {
        try {
            abrirCompartida();
        } catch (SQLException ex) {
            System.err.println("No se ha podido conectar a la base de datos\n" + ex.getMessage());
        }
    }

    /**
     * Cierra la conexion del proceso. Ojo: es compartida, asi que esto la cierra
     * para TODAS las pestañas; solo tiene sentido al terminar el programa.
     */
    public void close() {
        ConexionBD.cerrar();
        connect = null;
    }

    /**
     * Tablas de la base. El {@link ResultSet} queda abierto y el llamador DEBE
     * cerrarlo: mientras viva, SQLite mantiene un lock de lectura que bloquea
     * cualquier borrado. Con closeOnCompletion, cerrarlo cierra tambien su
     * sentencia y libera el lock.
     */
    public ResultSet cargarTablas() throws SQLException, IOException {
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        st.closeOnCompletion();
        return st.executeQuery();
    }

    public void borrarTabla(int ntabla) throws IOException, SQLException {
        if (ntabla == -1) {
            return;
        }
        try (PreparedStatement st = connect.prepareStatement(
                "delete from Tablas where numTabla = trim(?)")) {
            st.setInt(1, ntabla);
            st.execute();
        }
    }

    
    

}
