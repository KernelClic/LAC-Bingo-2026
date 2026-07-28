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
    private static Connection compartida;

    /** Espera antes de rendirse si la base esta ocupada (ms). */
    private static final int ESPERA_OCUPADA = 5000;

    private void abrirCompartida() throws SQLException {
        if (compartida == null || compartida.isClosed()) {
            compartida = DriverManager.getConnection("jdbc:sqlite:" + url);
            // Ante un choque momentaneo, esperar en vez de fallar en seco.
            try (Statement p = compartida.createStatement()) {
                p.execute("PRAGMA busy_timeout = " + ESPERA_OCUPADA);
            }
        }
        connect = compartida;
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
        try {
            if (compartida != null && !compartida.isClosed()) {
                compartida.close();
            }
            compartida = null;
            connect = null;
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
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
