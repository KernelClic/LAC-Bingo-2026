package Controlador;

import java.io.*;
import Modelo.*;

public class AccessFile {

    private static RandomAccessFile flujo;
    private static int numeroRegistros;
    private static final int tamañoRegistro = 1000;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final String RutaWinDB = "c:\\Bingo\\db\\";
    private static final String RutaLinDB = "/Bingo/db/";

    private static final String FileRutaWindb = "c:\\windows\\system\\windll.dll";
    private static final String FileRutaLindb = "/usr/readme.txt";

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
    
    public static boolean buscarFile(File archivo) throws IOException {
        if (!archivo.exists() || !archivo.isFile()) {
            return false;
        }
        // throw new IOException(" Error de Licencia comunquese con el Administrador. ");
        return true;
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
    public static void cerrar() throws IOException {
        flujo.close();
    }
    
    public static int getNumeroRegistros(){
        return numeroRegistros;
    }
        

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


    public static void añadirConf(Configuracion tabla) throws IOException {
        setConf(numeroRegistros, tabla);
            numeroRegistros++;
        }
    
    public static Configuracion getConf(int i) throws IOException {
        if (i >= 0 && i <= getNumeroRegistros()) {
            flujo.seek(i * tamañoRegistro);
            return new Configuracion(flujo.readInt(), flujo.readInt(), 
                    flujo.readUTF(),
                    flujo.readInt(),flujo.readUTF(),
                    flujo.readInt(),flujo.readUTF(),
                    flujo.readInt(),flujo.readUTF());//O
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
            return null;
        }
    }

    public static int buscarRegistro(int buscado) throws IOException {
        Configuracion p;
        if (buscado == 0) {
            return -1;
        }
        for (int i = 0; i < getNumeroRegistros(); i++) {
            flujo.seek(i * tamañoRegistro);
            p = getConf(i);
            if (p.getId()== buscado) {
                return i;
            }
        }
        return -1;
    }

}
