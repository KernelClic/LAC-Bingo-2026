package Controlador;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Licencia atada al equipo (node-locked).
 *
 * <p>El aplicativo solo se ejecuta en un computador donde exista una clave de
 * activación válida, previamente instalada. La clave se deriva de una huella
 * única del equipo (dirección MAC + nombre del equipo) combinada con un secreto
 * que solo conoce el Administrador del Sistema, por lo que copiar el archivo de licencia a otro
 * computador no funciona: cada equipo tiene una huella —y por tanto una clave—
 * distinta.</p>
 *
 * <p>Flujo:</p>
 * <ol>
 *   <li>El equipo expone un <b>ID de equipo</b> ({@link #idEquipo()}).</li>
 *   <li>El Administrador del Sistema genera la clave a partir de ese ID con el secreto
 *       ({@link #generarClaveDesdeId(String)}).</li>
 *   <li>El cliente ingresa la clave; si coincide con la esperada, se guarda
 *       cifrada-por-derivación en {@code licencia.lic} junto a la base de datos.</li>
 *   <li>En cada arranque se valida el archivo contra la huella actual.</li>
 * </ol>
 *
 * Funciona igual en Windows y en Linux (usa solo API estándar de Java).
 */
public final class Licencia {

    /** Secreto compartido — solo lo conoce el Administrador del Sistema. Eleva el nivel de seguridad. */
    private static final String SECRETO = "K3rnelCl1c::LAC-Bingo::2025::#n0d3-l0ck$";

    /** Nombre del archivo de licencia, guardado junto a la base de datos. */
    private static final String ARCHIVO = "licencia.lic";

    private Licencia() { }

    // =====================================================================
    // API pública
    // =====================================================================

    /** Ruta absoluta del archivo de licencia para el SO actual. */
    public static File archivoLicencia() {
        return new File(AccesoAleatorio.getRutaFileDB() + ARCHIVO);
    }

    /**
     * ID legible del equipo, derivado de la huella de hardware
     * (MAC + nombre del equipo). Es lo que el cliente reporta al Administrador del Sistema.
     */
    public static String idEquipo() {
        String hash = sha256Hex(huellaEquipo());
        return agrupar(hash.toUpperCase().substring(0, 16), 4); // XXXX-XXXX-XXXX-XXXX
    }

    /** Clave de activación válida para ESTE equipo. */
    public static String claveEsperada() {
        return generarClaveDesdeId(idEquipo());
    }

    /**
     * Genera la clave de activación a partir de un ID de equipo dado.
     * Usado tanto por la validación local como por el generador interno del Administrador del Sistema.
     */
    public static String generarClaveDesdeId(String id) {
        String norm = (id == null ? "" : id.trim().toUpperCase());
        String hash = sha256Hex(norm + "|" + SECRETO);
        return agrupar(hash.toUpperCase().substring(0, 20), 5); // XXXXX-XXXXX-XXXXX-XXXXX
    }

    /** {@code true} si el equipo ya tiene una licencia válida instalada. */
    public static boolean estaActivado() {
        try {
            File f = archivoLicencia();
            if (!f.exists()) return false;
            String guardada = new String(Files.readAllBytes(f.toPath()),
                    StandardCharsets.UTF_8).trim();
            return claveEsperada().equalsIgnoreCase(guardada);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Intenta activar el equipo con la clave ingresada. Si es la correcta para
     * este equipo, la guarda en disco y devuelve {@code true}.
     */
    public static boolean activar(String claveIngresada) {
        if (claveIngresada == null) return false;
        String limpia = claveIngresada.trim().toUpperCase();
        if (!claveEsperada().equalsIgnoreCase(limpia)) return false;
        try {
            File f = archivoLicencia();
            File dir = f.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();
            Files.write(f.toPath(), limpia.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // =====================================================================
    // Huella de hardware (MAC + nombre del equipo)
    // =====================================================================

    private static String huellaEquipo() {
        return primeraMac() + "|" + nombreEquipo();
    }

    /**
     * Primera dirección MAC física (descarta loopback, virtuales y point-to-point),
     * en orden determinista. Devuelve "NOMAC" si no hay ninguna disponible.
     */
    private static String primeraMac() {
        List<String> macs = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            while (ifs != null && ifs.hasMoreElements()) {
                NetworkInterface ni = ifs.nextElement();
                if (ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint()) continue;
                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length == 0) continue;
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) sb.append(String.format("%02X", b));
                macs.add(sb.toString());
            }
        } catch (Exception ignored) { }
        if (macs.isEmpty()) return "NOMAC";
        Collections.sort(macs);          // determinista aunque haya varias tarjetas
        return macs.get(0);
    }

    private static String nombreEquipo() {
        try {
            String h = InetAddress.getLocalHost().getHostName();
            if (h != null && !h.trim().isEmpty()) return h.trim();
        } catch (Exception ignored) { }
        String env = System.getenv("COMPUTERNAME");           // Windows
        if (env == null || env.isEmpty()) env = System.getenv("HOSTNAME"); // Linux
        return (env == null || env.isEmpty()) ? "NOHOST" : env.trim();
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("SHA-256 no disponible", ex);
        }
    }

    /** Inserta guiones cada {@code grupo} caracteres: "ABCD1234" → "ABCD-1234". */
    private static String agrupar(String s, int grupo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && i % grupo == 0) sb.append('-');
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
