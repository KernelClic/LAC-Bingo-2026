/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public Connection getConnect() {
        return connect;
    }

    public void setConnect(Connection connect) {
        this.connect = connect;
    }

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

    public String encriptarCaracter(String caracter, int variable, int indice) {
        int ind;
        if (patronBusqueda.indexOf(caracter) != -1) {
            ind = (patronBusqueda.indexOf(caracter) + variable + indice) % patronBusqueda.length();
            return patronEncripta.substring(ind, ind + 1);
        }
        return caracter;
    }

    public String encriptarCadena(String cadena) {
        String resultado = "";
        for (int pos = 0; pos < cadena.length(); pos++) {
            if (pos == 0) {
                resultado = encriptarCaracter(cadena.substring(pos, pos + 1), cadena.length(), pos);
            } else {
                resultado += encriptarCaracter(cadena.substring(pos, pos + 1), cadena.length(), pos);
            }
        }
        return resultado;
    }

    public String desencriptaCadena(String cadena) {
        String original = "";
        for (int pos = 0; pos < cadena.length(); pos++) {
            if (pos == 0) {
                original = desencriptaCaracter(cadena.substring(pos, pos + 1), cadena.length(), pos);
            } else {
                original += desencriptaCaracter(cadena.substring(pos, pos + 1), cadena.length(), pos);
            }
        }
        return original;
    }

    public String desencriptaCaracter(String caracter, int variable, int indice) {
        int ind = 0;
        if (patronEncripta.indexOf(caracter) != -1) {
            if ((patronEncripta.indexOf(caracter) - variable - indice) > 0) {
                ind = (patronEncripta.indexOf(caracter) - variable - indice) % patronEncripta.length();
            } else {
                ind = (patronBusqueda.length()) + ((patronEncripta.indexOf(caracter) - variable - indice) % patronEncripta.length());
            }
            ind = ind % patronEncripta.length();
            return patronBusqueda.substring(ind, ind + 1);
        } else {
            return caracter;
        }
    }

    public Bingo() throws IOException {
        this.setUrl(AccesoAleatorio.getRutaFileDB() + "tablas.db");
        if (!AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFileDB() + "tablas.db")) || !AccesoAleatorio.buscarFile(new File(AccesoAleatorio.getRutaFiledb()))) {
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
                //enunciado.execute("CREATE TABLE Bingo (app text, codigo text);");
                enunciado.execute(
                        "CREATE TABLE Bingo ("
                                + "app text, "
                                + "codigo text" 
                                + " );");
                 

                
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
            
            PreparedStatement 
            st2 = connect.prepareStatement("insert into Bingo (app,codigo)"
                                                    + " values (?,?)");
            st2.setString(1, encriptarCadena("Configurar"));
            st2.setString(2, encriptarCadena("Configurar"));
            st2.execute();

            PreparedStatement 
            st3 = connect.prepareStatement("insert into Bingo (app,codigo)"
                                                    + " values (?,?)");
            st3.setString(1, encriptarCadena("Licencia"));
            st3.setString(2, encriptarCadena("Licenciar"));
            st3.execute();
                 */

 /*
                Statement enunciado;
                enunciado = connect.createStatement();

                // CREAR UNA TABLA NUEVA, LA BORRA SI EXISTE
                enunciado.execute("DROP TABLE IF EXISTS Licencia;");
                //enunciado.execute("CREATE TABLE Bingo (app text, codigo text);");
                enunciado.execute(
                        "CREATE TABLE Licencia ( "
                        + "tlim text, "
                        + "flim text, "
                        + "plim text, "
                        + "glim text, "
                        + "rlim text, "
                        + "clim text, "
                        + "pact int, "
                        + "gact int, "
                        + "ract int, "
                        + "cact int "
                        + " );");

                PreparedStatement st0 = connect.prepareStatement("delete from Licencia");
                st0.execute();
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

    public Boolean autenticar(String app, String pass) throws SQLException, IOException, ParseException {

        Boolean Login = false;
        String appCifrado = encriptarCadena(app);
        String passCifrado = encriptarCadena(pass);

        String sql = "SELECT app, codigo FROM Bingo WHERE trim(app) = trim(?) and trim(codigo) = trim(?);";

        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement(sql);

        st.setString(1, appCifrado);
        st.setString(2, passCifrado);

        result = st.executeQuery();

        while (result.next()) {
            if (result.getString("app").equals(appCifrado)) {
                if (result.getString("codigo").equals(passCifrado) 
                        && validarLicencia(app)) {
                    Login = true;
                }
            }
        }

        return Login;

    }

    public void actualizarPassword(String app, String pass, String newpass) throws SQLException, IOException, ParseException {
        Boolean Login = false;

        if (autenticar(app, pass)) {
            PreparedStatement st = connect.prepareStatement("delete from Bingo where app = trim(?)");
            st.setString(1, encriptarCadena(app));
            st.execute();

            PreparedStatement st0 = connect.prepareStatement("insert into Bingo (app,codigo)"
                    + " values (?,?)");
            st0.setString(1, encriptarCadena(app));
            st0.setString(2, encriptarCadena(newpass));
            st0.execute();

        }

    }

    public void generarLicencia(String _tlim, String _flim,
            String _plim, String _glim, String _rlim, String _clim) throws SQLException, IOException {

        Boolean gen = false;

        /*
            tlim ( * / - )
            flim 01/01/9999
         */
        PreparedStatement st = connect.prepareStatement("delete from Licencia ");
        st.execute();

        PreparedStatement st0 = connect.prepareStatement("insert into Licencia (tlim, flim, plim, glim, rlim, clim, pact, gact, ract, cact)"
                + " values (?,?,?,?,?,?,?,?,?,?)");
        st0.setString(1, _tlim);
        st0.setString(2, "01/01/9999");
        st0.setString(3, _plim);
        st0.setString(4, _glim);
        st0.setString(5, _rlim);
        st0.setString(6, _clim);
        st0.setInt(7, 0);
        st0.setInt(8, 0);
        st0.setInt(9, 0);
        st0.setInt(10, 0);

        st0.execute();

    }

    public void actualizarLicencia(String _tlim, String _flim,
            String _plim, String _glim, String _rlim, String _clim,
            int _pact, int _gact, int _ract, int _cact
            ) throws SQLException, IOException {

        Boolean gen = false;

        /*
            tlim ( * / - )
            flim 01/01/9999
         */
        PreparedStatement st = connect.prepareStatement("delete from Licencia ");
        st.execute();

        PreparedStatement st0 = connect.prepareStatement("insert into Licencia (tlim, flim, plim, glim, rlim, clim, pact, gact, ract, cact)"
                + " values (?,?,?,?,?,?,?,?,?,?)");
        st0.setString(1, _tlim);
        st0.setString(2, _flim);
        st0.setString(3, _plim);
        st0.setString(4, _glim);
        st0.setString(5, _rlim);
        st0.setString(6, _clim);
        st0.setInt(7, _pact);
        st0.setInt(8, _gact);
        st0.setInt(9, _ract);
        st0.setInt(10,_cact);

        st0.execute();

    }
    
    public Boolean validarLicencia(String app) throws SQLException, IOException, ParseException {

        Boolean Estado = false;

        String sql = "SELECT tlim, flim, plim, glim, rlim, clim, pact, gact, ract, cact "
                + "FROM Licencia ;";

        ResultSet result = null;
        PreparedStatement st = connect.prepareStatement(sql);
        result = st.executeQuery();
       
        if (result.getString("tlim").equals("*") && result.getString("flim").equals("01/01/9999")) {
            return true;
        } else {
            if (result.getString("tlim").equals("-")) {

                String _tlim = result.getString("tlim");
                String _flim = result.getString("flim");
                String _plim = result.getString("plim");
                String _glim = result.getString("glim");
                String _rlim = result.getString("rlim");
                String _clim = result.getString("clim");

                int _pact = result.getInt("pact");
                int _gact = result.getInt("gact");
                int _ract = result.getInt("ract");
                int _cact = result.getInt("cact");

                // validamos fecha 
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                Date fecHoy = new Date();

                Date fecLic = formato.parse(result.getString("flim"));

                if (fecHoy.after(fecLic)) {
                    Estado = false;
                }

                // validamos Pantalla
                if (result.getInt("pact") < Integer.parseInt(result.getString("plim")) && app.equals("Pantalla")) {
                    // incrementamos el pact 
                    _pact ++;
                    Estado = true;
                }

                // validamos Generador
                if (result.getInt("gact") < Integer.parseInt(result.getString("glim")) && app.equals("Generador")) {
                    // incrementamos el gact 
                    _gact ++;
                    Estado = true;                    
                }

                // validamos Reporte
                if (result.getInt("ract") < Integer.parseInt(result.getString("rlim")) && app.equals("Reporte")) {
                    // incrementamos el ract 
                    _ract++;
                    Estado = true;                    
                }

                // validamos Configurar
                if (result.getInt("cact") < Integer.parseInt(result.getString("clim")) && app.equals("Configurar")) {
                    // incrementamos el cact 
                    _cact++;
                    Estado = true;                    
                }
                
                // validamos Configurar
                if (app.equals("Licencia")) {
                    // incrementamos el cact 
                    Estado = true;                    
                }
                
                actualizarLicencia (_tlim, _flim, 
                                    _plim, _glim, _rlim, _clim,
                                    _pact, _gact, _ract, _cact);

                return Estado;

            }

        }

        return false;

    }

}
