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

    public void saveTabla(Tabla tabla, int codificacion) {
        try {
            PreparedStatement st = connect.prepareStatement("insert into Tablas (numTabla, activo, "
                    + "n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,"
                    + "n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,"
                    + "n21,n22,n23,n24,n25,codigo)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            
            switch (codificacion){
                case 1: // Numeros 
                        st.setString(28, Integer.toString(tabla.getNumTabla()));
                break;    
                case 2: // Letras 
                        st.setString(28, generarCodigo(tabla.getNumTabla(),2));
                break;
                case 3: // Numeros y Letras
                        st.setString(28, generarCodigo(tabla.getNumTabla(),3));
                break;    
               
            }

            st.execute();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public String generarCodigo (int nTabla, int op) {
        
        Integer nT = nTabla;
        String original = nT.toString();
        String ret = original; 
        
        
        
        switch(op){
            case 2: 
                ret = ret.replace('0', 'W');
                ret = ret.replace('1', 'R');
                ret = ret.replace('2', 'T');
                ret = ret.replace('3', 'P');
                ret = ret.replace('4', 'X');
                ret = ret.replace('5', 'Z');
                ret = ret.replace('6', 'A');
                ret = ret.replace('7', 'K');
                ret = ret.replace('8', 'E');
                ret = ret.replace('9', 'H');
            break;
            case 3:
                if (ret.length() > 3){ 
                String mil = original.substring(0, original.length()-3 );
                
                System.out.println("Codigo: "+mil);
                
                mil = mil.replace('0', 'W');
                mil = mil.replace('1', 'R');
                mil = mil.replace('2', 'T');
                mil = mil.replace('3', 'P');
                mil = mil.replace('4', 'X');
                mil = mil.replace('5', 'Z');
                mil = mil.replace('6', 'A');
                mil = mil.replace('7', 'K');
                mil = mil.replace('8', 'E');
                mil = mil.replace('9', 'H');
                
                //System.out.println("Codigo: "+mil+original.substring(original.length()-2,original.length() ));
                ret = mil+original.substring(original.length()-3,original.length() );
                } 
                
            break;
        }
        
        return ret;
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
            t.setCodigo(result.getString("codigo"));
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
                t.setCodigo(result.getString("codigo"));
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

            // Pregunta si las tablas son iguales
            for (int f = 0; f < 25; f++) {
                for (int c = 0; c < buscado.size(); c++) {
                    if (buscado.elementAt(c).equals(Integer.toString(bingo[f]))) {
                        i++;
                    }
                }
            }

            if (i == 25) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Pleno"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cruz Pequeña"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cruz Grande"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Cuatro Esquinas"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Machetazo Izquierdo"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Machetazo Derecho"));
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

            // Pregunta si las tablas son iguales
            for (int c = 1; c < buscado.size(); c++) {
                if (buscado.elementAt(c).equals(Integer.toString(bingo[15]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[20]))
                        || buscado.elementAt(c).equals(Integer.toString(bingo[21]))) {
                    i++;
                }
            }

            if (i == 3) {
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Punta de Flecha"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Vertical Central"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Horizontal Central"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra T"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra L"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra X"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Pequeña"));
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
                vtablasWin.addElement(new Ganador(t.getNumTabla(), "Letra U Grande"));
            }
            i = 0;
        }
        return vtablasWin;
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

}
