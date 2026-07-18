package Controlador;

import java.io.*;
import Modelo.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessFile {

    private static RandomAccessFile flujo;
    private static int numeroRegistros;
    private static final int tamañoRegistro = 1000;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final String RutaWinDB = "c:\\Bingo\\db\\";
    private static final String RutaLinDB = "/Bingo/db/";

    //private static final String FileRutaWindb = "c:\\windows\\system\\windll.dll";
    private static final String FileRutaWindb = "c:\\windows\\system\\windl1.dll";
    //private static final String FileRutaLindb = "/usr/readme.txt";
    private static final String FileRutaLindb = "/usr/readMe.txt";

    // Licencia
    private static int numeroRegistroslic;
    private static final int tamañoRegistrolic = 1000;
    private static final String FileRutaWinLic = "c:\\windows\\system\\windl1.dll";
    private static final String FileRutaLinLic = "/home/oracle/readMeLic.txt";

    private static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    private static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static String getRutaFileDB() {
        if (isWindows()) {
            return RutaWinDB;
        } else if (isMac()) {
            return RutaLinDB;
        } else if (isUnix()) {
            return RutaLinDB;
        }
        return null;
    }

    public static String getRutaFiledb() {
        if (isWindows()) {
            return FileRutaWindb;
        } else if (isMac()) {
            return FileRutaLindb;
        } else if (isUnix()) {
            return FileRutaLindb;
        }
        return null;
    }

    //Licencia
    public static String getRutaFileDb() {
        if (isWindows()) {
            return FileRutaWinLic;
        } else if (isMac()) {
            return FileRutaLinLic;
        } else if (isUnix()) {
            return FileRutaLinLic;
        }
        return null;
    }

    public static int buscarRegistro(int buscado) throws IOException {
        Configuracion p;
        if (buscado == 0) {
            return -1;
        }
        for (int i = 0; i < getNumeroRegistros(); i++) {
            flujo.seek(i * tamañoRegistro);
            p = getConf(i);
            if (p.getId() == buscado) {
                return i;
            }
        }
        return -1;
    }

    public static boolean validarLic(File archivo,String exe) throws IOException, ParseException {
        Licencia lic = new Licencia();
        boolean val = true;

        if (!archivo.exists() || !archivo.isFile()) {
            val = false;
        }

        AccessFile.leerFileLic(new File(AccessFile.getRutaFileDb()));
        Licencia conf = getLic(AccessFile.buscarRegistroLic(1));
        if (conf != null) {
            lic.setId(conf.getId());
            lic.setFecha(conf.getFecha());
            lic.setNroIP(conf.getNroIP());
            lic.setNroIG(conf.getNroIG());
            lic.setNroIR(conf.getNroIR());
            lic.setNroIC(conf.getNroIC());
            lic.setRegNroIP(conf.getRegNroIP());
            lic.setRegNroIG(conf.getRegNroIG());
            lic.setRegNroIR(conf.getRegNroIR());
            lic.setRegNroIC(conf.getRegNroIC());
            
            if (!lic.getFecha().equalsIgnoreCase("01/01/9999")){
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                Date fecHoy = new Date();
                Date fecLic = formato.parse(lic.getFecha());
                if (fecHoy.after(fecLic)){
                    val=false;
                }
            }
            
            // Numero IP
            int nip, regip;
            try {
                   nip = Integer.parseInt(lic.getNroIP());
                   regip = Integer.parseInt(lic.getRegNroIP());
                   if (nip>0){
                        
                        if (regip>nip){
                            val=false;
                        }
                   }  
            } catch (NumberFormatException nfe) {
                    val=false;
            }

            // Numero IG
            int nig, regig;
            try {
                   nig = Integer.parseInt(lic.getNroIG());
                   regig = Integer.parseInt(lic.getRegNroIG());
                   if (nig>0){
                        
                        if (regig>nig){
                            val=false;
                        }
                   }  
            } catch (NumberFormatException nfe) {
                    val=false;
            }

            // Numero IR
            int nir, regir;
            try {
                   nir = Integer.parseInt(lic.getNroIR());
                   regir = Integer.parseInt(lic.getRegNroIR());
                   if (nir>0){
                        
                        if (regir>nir){
                            val=false;
                        }
                   }  
            } catch (NumberFormatException nfe) {
                    val=false;
            }

            // Numero IC
            int nic, regic;
            try {
                   nic = Integer.parseInt(lic.getNroIC());
                   regic = Integer.parseInt(lic.getRegNroIC());
                   if (nic>0){
                        
                        if (regic>nic){
                            val=false;
                        }
                   }  
            } catch (NumberFormatException nfe) {
                    val=false;
            }
        }
        AccessFile.cerrar();

        if (!val){
            //borrarFiles(new File(getRutaFiledb()),new File(getRutaFileDb()));
            return false;
        }else  {
            int regip = Integer.parseInt(lic.getRegNroIP());
            int regig = Integer.parseInt(lic.getRegNroIG());
            int regir = Integer.parseInt(lic.getRegNroIR());
            int regic = Integer.parseInt(lic.getRegNroIC());
            
            AccessFile.crearFileLicencia(new File(AccessFile.getRutaFileDb()));
            switch(exe){
                case "P": regip ++; break;    
                case "G": regig ++; break;    
                case "R": regir ++; break;    
                case "C": regic ++; break;    
                case "L": regic ++; break;    
            }

            AccessFile.añadirLic(new Licencia(conf.getId(),conf.getFecha(),
            conf.getNroIP(), conf.getNroIG(), conf.getNroIR(), conf.getNroIC(),
            Integer.toString(regip), Integer.toString(regig), Integer.toString(regir), Integer.toString(regic)));
                       
            
            AccessFile.cerrar();
        }
        return val;
    }

    public static boolean buscarFile(File archivo) throws IOException {
        if (!archivo.exists() || !archivo.isFile()) {
            return false;
        }
        // throw new IOException(" Error de Licencia comunquese con el Administrador. ");
        return true;
    }

    public static void borrarFiles(File archivo1, File archivo2) throws IOException {
        if (archivo1.exists() && !archivo1.isFile()) {
            throw new IOException(archivo1.getName() + " no es un archivo");
        }
        archivo1.delete();
        if (archivo2.exists() && !archivo2.isFile()) {
            throw new IOException(archivo2.getName() + " no es un archivo");
        }
        archivo2.delete();
    }

    
    public static void crearFileTablas(File archivo) throws IOException {
        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException(archivo.getName() + " no es un archivo");
        }
        archivo.delete();
        flujo = new RandomAccessFile(archivo, "rw");
        numeroRegistros = (int) Math.ceil(
                (double) flujo.length() / (double) tamañoRegistro);
    }

    public static void leerFileTablas(File archivo) throws IOException {
        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException(archivo.getName() + " no es un archivo");
        }
        //archivo.delete();
        flujo = new RandomAccessFile(archivo, "rw");
        numeroRegistros = (int) Math.ceil(
                (double) flujo.length() / (double) tamañoRegistro);
    }

    // Configuracion
    public static boolean setConf(int i, Configuracion tabla) throws IOException {
        if (i >= 0 && i <= getNumeroRegistros()) {
            if (tabla.getTamaño() > tamañoRegistro) {
                System.out.println("\nTamaño de registro excedido.");
            } else {
                flujo.seek(i * tamañoRegistro);
                flujo.writeInt(tabla.getId());
                flujo.writeInt(tabla.getIntento());
                flujo.writeUTF(tabla.getJuego());
                flujo.writeInt(tabla.getTab1());
                flujo.writeUTF(tabla.getTabla1());
                flujo.writeInt(tabla.getTab2());
                flujo.writeUTF(tabla.getTabla2());
                flujo.writeInt(tabla.getTab3());
                flujo.writeUTF(tabla.getTabla3());
                return true;
            }
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
        }
        return false;
    }

    public static Configuracion getConf(int i) throws IOException {
        if (i >= 0 && i <= getNumeroRegistros()) {
            flujo.seek(i * tamañoRegistro);
            return new Configuracion(flujo.readInt(), flujo.readInt(),
                    flujo.readUTF(),
                    flujo.readInt(), flujo.readUTF(),
                    flujo.readInt(), flujo.readUTF(),
                    flujo.readInt(), flujo.readUTF());//O
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
            return null;
        }
    }

    public static void añadirConf(Configuracion tabla) throws IOException {
        setConf(numeroRegistros, tabla);
        numeroRegistros++;
    }

    public static void cerrar() throws IOException {
        flujo.close();
    }

    public static int getNumeroRegistros() {
        return numeroRegistros;
    }

    // Licencia
    public static String getRutaFileLic() {
        if (isWindows()) {
            return FileRutaWinLic;
        } else if (isMac()) {
            return FileRutaLinLic;
        } else if (isUnix()) {
            return FileRutaLinLic;
        }
        return null;
    }

    // Licencia
    public static void crearFileLicencia(File archivo) throws IOException {
        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException(archivo.getName() + " no es un archivo");
        }
        archivo.delete();
        flujo = new RandomAccessFile(archivo, "rw");
        numeroRegistroslic = (int) Math.ceil(
                (double) flujo.length() / (double) tamañoRegistrolic);
    }

    // Licencia
    public static void leerFileLic(File archivo) throws IOException {
        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException(archivo.getName() + " no es un archivo");
        }
        //archivo.delete();
        flujo = new RandomAccessFile(archivo, "rw");
        numeroRegistroslic = (int) Math.ceil(
                (double) flujo.length() / (double) tamañoRegistrolic);
    }

    // Licencia
    public static int getNumeroRegistrosLic() {
        return numeroRegistroslic;
    }

    // Licencia  
    public static boolean setLic(int i, Licencia tabla) throws IOException {
        int x = getNumeroRegistrosLic();
        //if (i >= 0 && i <= getNumeroRegistrosLic()) {
        if (i >= 0 && i <= x) {
            if (tabla.getTamaño() > tamañoRegistrolic) {
                System.out.println("\nTamaño de registro excedido.");
            } else {
                flujo.seek(i * tamañoRegistrolic);
                flujo.writeInt(tabla.getId());
                flujo.writeUTF(tabla.getFecha());
                flujo.writeUTF(tabla.getNroIP());
                flujo.writeUTF(tabla.getNroIG());
                flujo.writeUTF(tabla.getNroIR());
                flujo.writeUTF(tabla.getNroIC());
                flujo.writeUTF(tabla.getRegNroIP());
                flujo.writeUTF(tabla.getRegNroIG());
                flujo.writeUTF(tabla.getRegNroIR());
                flujo.writeUTF(tabla.getRegNroIC());
                return true;
            }
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
        }
        return false;
    }

    // Licencia
    public static void añadirLic(Licencia tabla) throws IOException {
        setLic(numeroRegistroslic, tabla);
        numeroRegistroslic++;
    }

    //Licencia
    public static Licencia getLic(int i) throws IOException {
        int x = getNumeroRegistrosLic();
        //if (i >= 0 && i <= getNumeroRegistrosLic()) {
        if (i >= 0 && i <= x) {
            flujo.seek(i * tamañoRegistrolic);
            return new Licencia(flujo.readInt(), flujo.readUTF(), flujo.readUTF(), flujo.readUTF(),
                    flujo.readUTF(), flujo.readUTF(), flujo.readUTF(),
                    flujo.readUTF(), flujo.readUTF(), flujo.readUTF());//O
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");

            return null;
        }
    }

    // Licencia 
    public static int buscarRegistroLic(int buscado) throws IOException {
        Licencia p;
        if (buscado == 0) {
            return -1;
        }
        for (int i = 0; i < getNumeroRegistrosLic(); i++) {
            flujo.seek(i * tamañoRegistrolic);
            p = getLic(i);
            if (p.getId() == buscado) {
                return i;
            }
        }
        return -1;
    }


}
