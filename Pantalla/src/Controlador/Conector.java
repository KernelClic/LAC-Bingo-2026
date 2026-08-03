/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Ganador;
import Modelo.Tabla;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    // Modo programado (config.ker): evita duplicar los cartones pre-fijados del Pleno.
    public int plenoUnaVez = 0;

    public Conector() throws IOException {
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
                //System.out.println("Conectado");
                // CREAR Sentencia 
                Statement enunciado;
                enunciado = connect.createStatement();

                // Se crea la tabla SOLO si no existe. Antes hacia
                // "DROP TABLE IF EXISTS Tablas" primero, de modo que abrir el
                // programa borraba todos los cartones sin avisar. El borrado
                // intencional sigue estando en borrarBase(), que llama el boton
                // Generar.
                enunciado.execute("CREATE TABLE IF NOT EXISTS Tablas (numTabla int primary key, activo int, "
                        + "n1 int,n2 int,n3 int,n4 int,n5 int,"
                        + "n6 int,n7 int,n8 int,n9 int,n10 int,"
                        + "n11 int,n12 int,n13 int,n14 int,n15 int,"
                        + "n16 int,n17 int,n18 int,n19 int,n20 int,"
                        + "n21 int,n22 int,n23 int,n24 int,n25 int, codigo text);");
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

    public void borrarBase() throws SQLException {
        Statement enunciado;
        enunciado = connect.createStatement();
        enunciado.execute("delete from Tablas");
    }

    public void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveTabla(Tabla tabla) {
        try {
            PreparedStatement st = connect.prepareStatement("insert into Tablas (numTabla, activo, "
                    + "n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,"
                    + "n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,"
                    + "n21,n22,n23,n24,n25)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            st.setInt(1, tabla.getNumTabla());
            st.setInt(2, (tabla.isActivo()) ? 1 : 0);
            st.setInt(3, tabla.getN1());
            st.setInt(4, tabla.getN2());
            st.setInt(5, tabla.getN3());
            st.setInt(6, tabla.getN4());
            st.setInt(7, tabla.getN5());
            st.setInt(8, tabla.getN6());
            st.setInt(9, tabla.getN7());
            st.setInt(10, tabla.getN8());
            st.setInt(11, tabla.getN9());
            st.setInt(12, tabla.getN10());
            st.setInt(13, tabla.getN11());
            st.setInt(14, tabla.getN12());
            st.setInt(15, tabla.getN13());
            st.setInt(16, tabla.getN14());
            st.setInt(17, tabla.getN15());
            st.setInt(18, tabla.getN16());
            st.setInt(19, tabla.getN17());
            st.setInt(20, tabla.getN18());
            st.setInt(21, tabla.getN19());
            st.setInt(22, tabla.getN20());
            st.setInt(23, tabla.getN21());
            st.setInt(24, tabla.getN22());
            st.setInt(25, tabla.getN23());
            st.setInt(26, tabla.getN24());
            st.setInt(27, tabla.getN25());

            st.execute();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public int buscarTabla(int[] buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        int i = 0;
        int bingo[] = new int[25];
        if (buscado == null) {
            return -1;
        }

        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            if (result.getInt("activo") == 1) {
                t.setActivo(true);
            } else {
                t.setActivo(false);
            }

            bingo[0] = result.getInt("n1");
            bingo[1] = result.getInt("n2");
            bingo[2] = result.getInt("n3");
            bingo[3] = result.getInt("n4");
            bingo[4] = result.getInt("n5");
            bingo[5] = result.getInt("n6");
            bingo[6] = result.getInt("n7");
            bingo[7] = result.getInt("n8");
            bingo[8] = result.getInt("n9");
            bingo[9] = result.getInt("n10");
            bingo[10] = result.getInt("n11");
            bingo[11] = result.getInt("n12");
            bingo[12] = result.getInt("n13");
            bingo[13] = result.getInt("n14");
            bingo[14] = result.getInt("n15");
            bingo[15] = result.getInt("n16");
            bingo[16] = result.getInt("n17");
            bingo[17] = result.getInt("n18");
            bingo[18] = result.getInt("n19");
            bingo[19] = result.getInt("n20");
            bingo[20] = result.getInt("n21");
            bingo[21] = result.getInt("n22");
            bingo[22] = result.getInt("n23");
            bingo[23] = result.getInt("n24");
            bingo[24] = result.getInt("n25");

            for (int f = 0; f < 25; f++) {
                for (int c = 0; c < 25; c++) {
                    if (bingo[f] == buscado[c]) {
                        i++;
                    }
                }
            }
        }
        if (i == 25) {
            return -1;
        }

        return 0;
    }

    public Tabla getTabla(int ntabla) throws IOException {
        Tabla t = new Tabla();
        ResultSet result = null;
        try {
            PreparedStatement st = connect.prepareStatement("select * from Tablas where numTabla = ? ");
            st.setInt(1, ntabla);
            result = st.executeQuery();

            while (result.next()) {
                t.setNumTabla(result.getInt("numTabla"));
                if (result.getInt("activo") == 1) {
                    t.setActivo(true);
                } else {
                    t.setActivo(false);
                }

                t.setN1(result.getInt("n1"));
                t.setN2(result.getInt("n2"));
                t.setN3(result.getInt("n3"));
                t.setN4(result.getInt("n4"));
                t.setN5(result.getInt("n5"));
                t.setN6(result.getInt("n6"));
                t.setN7(result.getInt("n7"));
                t.setN8(result.getInt("n8"));
                t.setN9(result.getInt("n9"));
                t.setN10(result.getInt("n10"));
                t.setN11(result.getInt("n11"));
                t.setN12(result.getInt("n12"));
                t.setN13(result.getInt("n13"));
                t.setN14(result.getInt("n14"));
                t.setN15(result.getInt("n15"));
                t.setN16(result.getInt("n16"));
                t.setN17(result.getInt("n17"));
                t.setN18(result.getInt("n18"));
                t.setN19(result.getInt("n19"));
                t.setN20(result.getInt("n20"));
                t.setN21(result.getInt("n21"));
                t.setN22(result.getInt("n22"));
                t.setN23(result.getInt("n23"));
                t.setN24(result.getInt("n24"));
                t.setN25(result.getInt("n25"));
                t.setCodigo(result.getString("codigo"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return t;
    }

    public void borrarTabla(int ntabla) throws IOException {
        Tabla t = new Tabla();
        ResultSet result = null;
        try {
            PreparedStatement st = connect.prepareStatement("delete from Tablas where numTabla = ? ");
            st.setInt(1, ntabla);
            result = st.executeQuery();
            connect.commit();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Vector<Ganador> verificarPleno(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;
        int bingo[] = new int[25];
        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int f = 0; f < 25; f++) {
                for (int c = 0; c < buscado.size(); c++) {
                    if (buscado.elementAt(c).equals(Integer.toString(bingo[f]))) {
                        i++;
                    }
                }
            }

            if (i == 25) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarCruzPequeña(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();

        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cruz Pequeña", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarCruzGrande(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cruz Grande", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarCuatroEsquinas(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cuatro Esquinas", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarMachetazoIzquierdo(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Machetazo Izquierdo", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarMachetazoDerecho(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Machetazo Derecho", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarPuntadeFlecha(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))) {
                    i++;
                }
            }

            if (i == 3) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Punta de Flecha", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarVerticalCentral(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Vertical Central", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarHorizontalCentral(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))) {
                    i++;
                }
            }

            if (i == 4) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Horizontal Central", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraT(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))) {
                    i++;
                }
            }

            if (i == 8) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra T", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraL(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))) {
                    i++;
                }
            }

            if (i == 9) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraX(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))) {
                    i++;
                }
            }

            if (i == 8) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra X", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraUPequeña(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))) {
                    i++;
                }
            }

            if (i == 7) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Pequeña", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraUGrande(Vector buscado) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))) {
                    i++;
                }
            }

            if (i == 13) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Grande", t.getCodigo()));
            }
            i = 0;
        }
        return vtablasWin;
    }

    /**
     * true si la tabla pre-fijada esta realmente configurada. Sin esta
     * comprobacion se anunciaba como ganadora la casilla vacia y en la ventana
     * de ganadores aparecia un "-1" fantasma.
     */
    private static boolean esTablaPrefijada(String t) {
        return t != null && !t.trim().isEmpty()
                && !t.equalsIgnoreCase("-1") && !t.equalsIgnoreCase("N/A");
    }

    public int[] getVectorBingo(ResultSet rst) throws SQLException {
        int ret[] = new int[25];

        ret[0] = rst.getInt("n1");
        ret[1] = rst.getInt("n2");
        ret[2] = rst.getInt("n3");
        ret[3] = rst.getInt("n4");
        ret[4] = rst.getInt("n5");
        ret[5] = rst.getInt("n6");
        ret[6] = rst.getInt("n7");
        ret[7] = rst.getInt("n8");
        ret[8] = rst.getInt("n9");
        ret[9] = rst.getInt("n10");
        ret[10] = rst.getInt("n11");
        ret[11] = rst.getInt("n12");
        ret[12] = rst.getInt("n13");
        ret[13] = rst.getInt("n14");
        ret[14] = rst.getInt("n15");
        ret[15] = rst.getInt("n16");
        ret[16] = rst.getInt("n17");
        ret[17] = rst.getInt("n18");
        ret[18] = rst.getInt("n19");
        ret[19] = rst.getInt("n20");
        ret[20] = rst.getInt("n21");
        ret[21] = rst.getInt("n22");
        ret[22] = rst.getInt("n23");
        ret[23] = rst.getInt("n24");
        ret[24] = rst.getInt("n25");

        return ret;
    }

    // =====================================================================
    // Figuras configurables desde archivo (/Bingo/db/matriz.txt)
    // =====================================================================

    /**
     * Carga las figuras a jugar desde {@code <db>/matriz.txt} (patrones 5x5 de
     * 'X'/'0', una fila por línea, cada figura terminada por una línea "-----").
     * Rellena la tabla SQLite {@code figuras(id, matriz)} y devuelve la lista de
     * matrices (texto con saltos de línea). Ruta resuelta por SO vía AccesoAleatorio.
     */
    /** Nombre interno de cada figura (paralelo a obtenerImagenesPredisenadas). */
    private List<String> nombresFiguras = new ArrayList<>();
    /** Nombre a mostrar/traduccion de cada figura ("" si no tiene). */
    private List<String> nombresMostrar = new ArrayList<>();

    public List<String> getNombresFiguras() {
        return nombresFiguras;
    }

    public List<String> getNombresMostrar() {
        return nombresMostrar;
    }

    public List<String> obtenerImagenesPredisenadas() throws SQLException, IOException {
        List<String> matrices = new ArrayList<>();
        nombresFiguras = new ArrayList<>();
        nombresMostrar = new ArrayList<>();
        File file = new File(AccesoAleatorio.getRutaFileDB() + "matriz.txt");
        if (!file.exists()) {
            return matrices;
        }
        try (PreparedStatement crea = connect.prepareStatement(
                "CREATE TABLE IF NOT EXISTS figuras (id INTEGER PRIMARY KEY, matriz TEXT) ")) {
            crea.execute();
        }
        try (Statement del = connect.createStatement()) {
            del.executeUpdate("DELETE FROM figuras");
        }

        // UTF-8 EXPLICITO. Sin indicarlo, Scanner usa la codificacion por defecto
        // del sistema: en Linux es UTF-8 y todo cuadra, pero en Windows es
        // windows-1252 y los nombres con acento o ñ se leian mal
        // ("Cruz Pequeña" -> "Cruz PequeÃ±a"). Como la configuracion por figura
        // se guarda en config.ker indexada POR NOMBRE, la busqueda fallaba y la
        // partida programada no se aplicaba en Windows.
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            String matriz = "";
            String nombre = null;    // "# Nombre" (parte antes de '|')
            String mostrar = "";     // traduccion (parte despues de '|')
            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    String cab = line.substring(1).trim();
                    int p = cab.indexOf('|');
                    if (p >= 0) {
                        nombre = cab.substring(0, p).trim();
                        mostrar = cab.substring(p + 1).trim();
                    } else {
                        nombre = cab;
                        mostrar = "";
                    }
                } else if (!line.equals("-----")) {
                    matriz += line + "\n";
                } else {
                    insertarFigura(id, matriz);
                    matrices.add(matriz);
                    nombresFiguras.add((nombre != null && !nombre.isEmpty()) ? nombre : ("Figura " + id));
                    nombresMostrar.add(mostrar);
                    id++; matriz = ""; nombre = null; mostrar = "";
                }
            }
            // Última figura si el archivo no termina en "-----".
            if (!matriz.trim().isEmpty()) {
                insertarFigura(id, matriz);
                matrices.add(matriz);
                nombresFiguras.add((nombre != null && !nombre.isEmpty()) ? nombre : ("Figura " + id));
                nombresMostrar.add(mostrar);
            }
        }
        return matrices;
    }

    private void insertarFigura(int id, String matriz) throws SQLException {
        try (PreparedStatement pstmt = connect.prepareStatement(
                "INSERT INTO figuras(id, matriz) VALUES(?,?)")) {
            pstmt.setInt(1, id);
            pstmt.setString(2, matriz);
            pstmt.executeUpdate();
        }
    }

    /**
     * Guarda la lista de figuras en {@code <db>/matriz.txt} con el formato
     * "# Nombre | NombreMostrar" + 5 filas (X/0) + "-----" por figura.
     * Si la traduccion esta vacia, se escribe solo "# Nombre". Reemplaza el archivo.
     */
    public void guardarFiguras(List<String> nombres, List<String> mostrar,
                               List<String> matricesLista) throws IOException {
        File file = new File(AccesoAleatorio.getRutaFileDB() + "matriz.txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matricesLista.size(); i++) {
            String nom = (nombres != null && i < nombres.size() && nombres.get(i) != null
                    && !nombres.get(i).trim().isEmpty()) ? nombres.get(i).trim() : ("Figura " + (i + 1));
            String tra = (mostrar != null && i < mostrar.size() && mostrar.get(i) != null)
                    ? mostrar.get(i).trim() : "";
            String m = matricesLista.get(i);
            if (m == null) m = "";
            sb.append("# ").append(nom);
            if (!tra.isEmpty()) sb.append(" | ").append(tra);
            sb.append("\n");
            sb.append(m);
            if (!m.endsWith("\n")) sb.append("\n");
            sb.append("-----\n");
        }
        try (java.io.FileWriter w = new java.io.FileWriter(file)) {
            w.write(sb.toString());
        }
    }

    /**
     * Detecta ganadores para una figura definida por {@code posiciones} (índices
     * 0..24 column-major de las celdas marcadas, mismas que usa getVectorBingo).
     * Gana la tabla cuyos números cantados ({@code buscado}) cubren TODAS esas
     * posiciones. {@code nombre} identifica la figura en el Ganador resultante.
     */
    /**
     * Regla universal de la partida programada: el amaño no puede anunciarse
     * antes de que el tablero permita una victoria legitima de esa figura.
     *
     * <p>Cada letra del BINGO cubre 15 numeros (B=1-15, I=16-30, N=31-45,
     * G=46-60, O=61-75). Para completar una figura hay que haber cantado, en
     * cada letra, al menos tantas balotas como casillas tenga la figura en esa
     * columna. Un ganador REAL cumple eso por definicion; exigirselo tambien a
     * las tablas pre-fijadas hace que el premio sea indistinguible de una
     * victoria normal. En el Pleno la condicion equivale a B>=5, I>=5, N>=4,
     * G>=5 y O>=5 (el centro de la N es libre y no se canta).</p>
     *
     * @param posiciones casillas de la figura (indice = fila + 5*columna)
     * @param cantados   numeros ya cantados en el tablero
     * @return true si el reparto por letra permite ganar esa figura
     */
    private static boolean tableroPermiteFigura(List<Integer> posiciones,
            java.util.Set<String> cantados) {
        int[] necesita = new int[5];
        for (Integer pos : posiciones) {
            if (pos == null || pos < 0 || pos > 24 || pos == 12) {
                continue;                        // centro libre: no se canta
            }
            necesita[pos / 5]++;
        }
        int[] hay = new int[5];
        for (String c : cantados) {
            try {
                int n = Integer.parseInt(c.trim());
                if (n >= 1 && n <= 75) {
                    hay[(n - 1) / 15]++;
                }
            } catch (NumberFormatException ex) {
                // el centinela "-1" y cualquier basura se ignoran
            }
        }
        for (int col = 0; col < 5; col++) {
            if (hay[col] < necesita[col]) {
                return false;
            }
        }
        return true;
    }

    public Vector verificarArchivo(Vector buscado, List<Integer> posiciones, String nombre)
            throws SQLException, IOException {
        return verificarArchivo(buscado, posiciones, nombre, "-1", "-1");
    }

    /**
     * Igual que {@link #verificarArchivo(Vector, List, String)} pero con la
     * ruta de partida programada: cuando a un carton le falta UNA casilla para
     * completar la figura, se anuncian las tablas pre-fijadas t10/t11.
     *
     * Las figuras de matriz.txt no tenian esta variante, asi que el amaño
     * configurado nunca se aplicaba al jugar con ellas — que es la forma
     * habitual de jugar. Misma condicion que las figuras fijas: faltando una.
     */
    public Vector verificarArchivo(Vector buscado, List<Integer> posiciones, String nombre,
            String t10, String t11) throws SQLException, IOException {
        return verificarArchivo(buscado, posiciones, nombre, t10, t11, null);
    }

    /**
     * Variante completa de la partida programada para figuras de matriz.txt:
     *
     * <ul>
     *   <li>{@code t10/t11}: premiadas cuando falta UNA casilla.</li>
     *   <li>{@code completa}: premiadas cuando la figura se COMPLETA. Es la
     *       modalidad del viejo registro 2, que solo alcanzaba al checkbox
     *       "Pleno"; ahora vale para cualquier figura.</li>
     * </ul>
     *
     * Las de "al completar" se anuncian UNA sola vez por verificacion, aunque
     * varios cartones completen a la vez.
     */
    public Vector verificarArchivo(Vector buscado, List<Integer> posiciones, String nombre,
            String t10, String t11, String[] completa) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        if (buscado == null || posiciones == null || posiciones.isEmpty()) {
            return vtablasWin;
        }
        // Las pre-fijadas se anuncian UNA sola vez por verificacion. Sin esto,
        // cada carton que califica volvia a anunciarlas: en figuras cortas
        // (Pinos son 4 casillas) varios quedan a una a la vez y la ventana de
        // ganadores mostraba la misma tabla repetida.
        boolean completaAnunciada = false;
        boolean faltaAnunciada = false;
        // Conjunto de numeros cantados (sin el centinela "-1" del indice 0).
        java.util.HashSet<String> cantados = new java.util.HashSet<>();
        for (int c = 1; c < buscado.size(); c++) {
            if (buscado.elementAt(c) != null) cantados.add(buscado.elementAt(c).toString());
        }
        // Regla universal: sin el reparto por letra que exige la figura, el
        // amaño no sale (se veria que el premio no pudo ganarse de verdad).
        boolean permiteAmaño = tableroPermiteFigura(posiciones, cantados);
        // try-with-resources: cierra statement y resultset (evita fuga JDBC -> OOM).
        try (PreparedStatement st = connect.prepareStatement("select * from Tablas");
             ResultSet result = st.executeQuery()) {
            while (result.next()) {
                t.setNumTabla(result.getInt("numTabla"));
                int bingo[] = this.getVectorBingo(result);
                t.setCodigo(result.getString("codigo"));
                // Gana si CADA celda del patron esta cubierta: cantada, o es la
                // casilla central libre (valor -1, que siempre cuenta como marcada).
                int faltan = 0;
                for (Integer posicion : posiciones) {
                    if (posicion == null || posicion < 0 || posicion > 24) continue;
                    int val = bingo[posicion];
                    if (val == -1) continue;                 // centro libre
                    if (!cantados.contains(Integer.toString(val))) faltan++;
                }
                if (faltan == 0) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), nombre, t.getCodigo()));
                    if (!completaAnunciada && completa != null) {
                        for (String c : completa) {
                            if (esTablaPrefijada(c)) {
                                vtablasWin.addElement(new Ganador(t.getNumTabla(), nombre, c));
                            }
                        }
                        completaAnunciada = true;
                    }
                } else if (faltan == 1 && !faltaAnunciada && permiteAmaño) {
                    // Partida programada: al faltar UNA casilla, entran las pre-fijadas.
                    boolean alguna = false;
                    if (esTablaPrefijada(t10)) {
                        vtablasWin.addElement(new Ganador(t.getNumTabla(), nombre, t10));
                        alguna = true;
                    }
                    if (esTablaPrefijada(t11)) {
                        vtablasWin.addElement(new Ganador(t.getNumTabla(), nombre, t11));
                        alguna = true;
                    }
                    faltaAnunciada = alguna;
                }
            }
        }
        return vtablasWin;
    }



    // ==== Sobrecargas "partida programada" (config.ker): fuerzan a ganar
    //      los cartones pre-fijados (t10/t11) cuando faltan a 1 casilla de la figura.
    //      Portadas desde v01. Solo se invocan en modo programado. ====

    public Vector<Ganador> verificarPleno(Vector buscado, String t01, String t02, String t03, String t10, String t11) throws IOException, SQLException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;
        int bingo[] = new int[25];
        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int f = 0; f < 25; f++) {
                for (int c = 0; c < buscado.size(); c++) {
                    if (buscado.elementAt(c).equals(Integer.toString(bingo[f]))) {
                        i++;
                    }
                }
            }

            if (i == 25) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t.getCodigo()));
                if (plenoUnaVez == 0) {
                    if (!t01.isEmpty() && !t01.equalsIgnoreCase("-1") && !t01.equalsIgnoreCase("N/A")) {
                        vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t01));
                    }
                    if (!t02.isEmpty() && !t02.equalsIgnoreCase("-1") && !t02.equalsIgnoreCase("N/A")) {
                        vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t02));
                    }
                    if (!t03.isEmpty() && !t03.equalsIgnoreCase("-1") && !t03.equalsIgnoreCase("N/A")) {
                        vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t03));
                    }
                    plenoUnaVez = 1;
                }
            }

            if ((i == 24) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1") && !t10.equalsIgnoreCase("N/A")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1") && !t11.equalsIgnoreCase("N/A")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraT(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))) {
                    i++;
                }
            }

            if (i == 8) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra T", t.getCodigo()));
            }

            if ((i == 7) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra T", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra T", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraL(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))) {
                    i++;
                }
            }

            if (i == 9) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L", t.getCodigo()));
            }

            if ((i == 8) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraX(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))) {
                    i++;
                }
            }

            if (i == 8) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra X", t.getCodigo()));
            }
            
            
            if ((i == 7) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra X", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra X", t11));
                }
            }

            
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraO(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        ) {
                    i++;
                }
            }

            if (i == 16) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra O", t.getCodigo()));
            }

            if ((i == 15) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra O", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra O", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraN(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        ) {
                    i++;
                }
            }

            if (i == 12) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra N", t.getCodigo()));
            }

            if ((i == 11) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra N", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra N", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraC(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        ) {
                    i++;
                }
            }

            if (i == 13) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra C", t.getCodigo()));
            }

            if ((i == 12) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra C", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra C", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraH(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {

                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        ) {
                    i++;
                }
            }

            if (i == 12) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra H", t.getCodigo()));
            }

            if ((i == 11) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra H", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra H", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraI(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        ) {
                    i++;
                }

            }

            if (i == 12) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra I", t.getCodigo()));
            }

            if ((i == 11) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra I", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra I", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraZ(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))                        
                        ) {
                    i++;
                }
            }

            if (i == 12) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Z", t.getCodigo()));
            }
                        
            if ((i == 11) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Z", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Z", t11));
                }
            }

            
            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraLinvertida(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                    if (buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))) {
                i++;
                }
            }

            if (i == 9) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L Invertida", t.getCodigo()));
            }

            if ((i == 8) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L Invertida", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L Invertida", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraS(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))) {
                i++;
                }
            }

            if (i == 16) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra S", t.getCodigo()));
            }

            if ((i == 15) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra S", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra S", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraE(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[5]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))) {
                i++;
                }
            }

            if (i == 14) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra E", t.getCodigo()));
            }

            if ((i == 13) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra E", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra E", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraCasita(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[10]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))) {
                i++;
                }
            }

            if (i == 5) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Casita", t.getCodigo()));
            }

            if ((i == 4) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Casita", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Casita", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

    public Vector verificarLetraCuadrado(Vector buscado, String t10, String t11) throws SQLException, IOException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));
            
            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[6]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[7]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[8]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[11]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[13]))                        
                        || buscado.elementAt(c).equals(Integer.toString(bingo[16]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[17]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[18]))){
                        
                i++;
                }
            }

            if (i == 8) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Cuadrado", t.getCodigo()));

            }
                
            if ((i == 7) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Cuadrado", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra Cuadrado", t11));
                }
            }
            
            
    
            i = 0;
        }
        return vtablasWin;
    }

    public Vector<Ganador> verificarLetraUGrande(Vector buscado, String t10, String t11) throws IOException, SQLException {
        Tabla t = new Tabla();
        Vector<Ganador> vtablasWin = new Vector<Ganador>();
        int i = 0, wt = 0;

        int bingo[] = new int[25];

        if (buscado == null) {
            return null;
        }
        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement("select * from Tablas");
        result = st.executeQuery();
        while (result.next()) {
            t.setNumTabla(result.getInt("numTabla"));
            bingo = this.getVectorBingo(result);
            t.setCodigo(result.getString("codigo"));

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[0]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[1]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[2]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[3]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[4]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[9]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[14]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[19]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[24]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[23]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[22]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))) {
                    i++;
                }
            }

            if (i == 13) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Grande", t.getCodigo()));
            }

            if ((i == 12) && ((!t10.isEmpty() && !t10.equalsIgnoreCase("-1")) || (!t11.isEmpty() && !t11.equalsIgnoreCase("-1")))) {
                if (esTablaPrefijada(t10)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Grande", t10));
                }
                if (esTablaPrefijada(t11)) {
                    vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Grande", t11));
                }
            }

            i = 0;
        }
        return vtablasWin;
    }

}
