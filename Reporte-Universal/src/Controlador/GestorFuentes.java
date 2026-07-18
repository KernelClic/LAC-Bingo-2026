package Controlador;

import Modelo.EstiloTexto;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Descubre y registra las fuentes disponibles para el reporte:
 *  - Las fuentes estándar PDF (Helvetica, Times, Courier...) que OpenPDF trae de fábrica.
 *  - Las fuentes del sistema operativo (carpetas de fuentes del SO).
 *  - Las fuentes personalizadas precargadas en {@code /Bingo/Fuentes}.
 *
 * Las fuentes TTF/OTF se embeben en el PDF (IDENTITY_H + EMBEDDED) para que el
 * documento se vea igual en cualquier equipo; las estándar usan WINANSI sin embeber.
 *
 * Singleton: el registro (lectura de cabeceras de ~200 fuentes) se hace una sola vez.
 */
public class GestorFuentes {

    private static GestorFuentes instancia;

    /** Familias listas para mostrar en los combos (título-case, ordenadas). */
    private final String[] familias;

    /** Familias provenientes de archivos TTF/OTF (en minúscula) → se embeben. */
    private final Set<String> familiasTTF = new TreeSet<>();

    /** Caché de viabilidad de incrustado por "fuente|estilo" (true = se puede embeber). */
    private final Map<String, Boolean> embedCache = new ConcurrentHashMap<>();

    public static synchronized GestorFuentes get() {
        if (instancia == null) instancia = new GestorFuentes();
        return instancia;
    }

    private GestorFuentes() {
        // Familias estándar que OpenPDF ya tiene registradas antes de añadir nada.
        Set<String> estandar = lower(FontFactory.getRegisteredFamilies());

        for (String dir : directoriosFuentes()) {
            File d = new File(dir);
            if (d.isDirectory()) {
                try { FontFactory.registerDirectory(dir, true); }
                catch (Exception ignored) { /* fuentes corruptas: se omiten */ }
            }
        }

        // Lo que apareció de más respecto a las estándar son las familias TTF/OTF.
        Set<String> todas = lower(FontFactory.getRegisteredFamilies());
        for (String f : todas)
            if (!estandar.contains(f)) familiasTTF.add(f);

        // Lista para los combos: todas las familias, título-case y ordenadas.
        TreeSet<String> display = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String f : todas) display.add(tituloCase(f));
        display.add("Helvetica"); // garantizar un default siempre disponible
        familias = display.toArray(new String[0]);
    }

    /** Nombres de familia para poblar los JComboBox. */
    public String[] listarFuentes() {
        return familias;
    }

    /** Nombre de fuente por defecto, siempre presente. */
    public String fuentePorDefecto() {
        return "Helvetica";
    }

    /**
     * Construye la {@link Font} de OpenPDF para un estilo dado, embebiendo la
     * fuente si proviene de archivo TTF/OTF.
     */
    public Font fontPDF(EstiloTexto e) {
        Color color = e.color != null ? e.color : Color.BLACK;
        boolean ttf = esEmbebida(e.fuente);
        // Si es incrustable PERO no se puede subset/embeber (p.ej. OTF-CFF como
        // fuentes matemáticas), caer a Helvetica para no romper la generación.
        if (ttf && !puedeEmbeber(e.fuente, e.styleFlags(), e.tamaño)) ttf = false;

        String enc = ttf ? BaseFont.IDENTITY_H : BaseFont.WINANSI;
        String nombre = ttf ? e.fuente : BaseFont.HELVETICA;
        Font f = FontFactory.getFont(nombre, enc, ttf, e.tamaño, e.styleFlags(), color);
        // Salvaguarda final: si aún así no resolvió, Helvetica estándar.
        if (f == null || f.getBaseFont() == null) {
            f = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.WINANSI, false,
                    e.tamaño, e.styleFlags(), color);
        }
        return f;
    }

    /** ¿La familia se embeberá (es TTF/OTF de SO o personalizada)? */
    public boolean esEmbebida(String fuente) {
        return fuente != null && familiasTTF.contains(fuente.toLowerCase());
    }

    /**
     * Comprueba (y cachea) si una fuente puede incrustarse realmente generando
     * un PDF mínimo de prueba. Algunas OTF con contornos CFF rompen el subsetting
     * de OpenPDF ("NewSubrsIndexNonCID is null"); aquí se detectan sin romper.
     */
    public boolean puedeEmbeber(String fuente, int style, float tamaño) {
        if (!esEmbebida(fuente)) return false;
        String key = fuente.toLowerCase() + "|" + style;
        Boolean ok = embedCache.get(key);
        if (ok != null) return ok;
        boolean res;
        try {
            Font f = FontFactory.getFont(fuente, BaseFont.IDENTITY_H, true,
                    tamaño > 0 ? tamaño : 12f, style, Color.BLACK);
            if (f == null || f.getBaseFont() == null) {
                res = false;
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Document d = new Document();
                PdfWriter.getInstance(d, bos);
                d.open();
                d.add(new Paragraph("Prueba 0123456789 AÁÉÍÑacz $.,-", f));
                d.close();   // aquí ocurre el subset → reproduce el fallo si lo hay
                res = true;
            }
        } catch (Throwable t) {
            res = false;
        }
        embedCache.put(key, res);
        return res;
    }

    /**
     * De una lista de estilos, devuelve los nombres de fuente que NO se pueden
     * incrustar (se sustituirán por Helvetica al generar). Para avisar al usuario.
     */
    public List<String> fuentesNoIncrustables(List<EstiloTexto> estilos) {
        Set<String> bad = new LinkedHashSet<>();
        for (EstiloTexto e : estilos) {
            if (e != null && esEmbebida(e.fuente)
                    && !puedeEmbeber(e.fuente, e.styleFlags(), e.tamaño)) {
                bad.add(e.fuente);
            }
        }
        return new ArrayList<>(bad);
    }

    // ----------------------------------------------------------------------

    /** Directorios de fuentes según el sistema operativo + carpeta de la app. */
    private static String[] directoriosFuentes() {
        String so   = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home", "");
        if (so.contains("win")) {
            String winDir = System.getenv("WINDIR");
            if (winDir == null) winDir = "C:\\Windows";
            return new String[]{
                winDir + "\\Fonts",
                home + "\\AppData\\Local\\Microsoft\\Windows\\Fonts",
                "/Bingo/Fuentes", "/Bingo/fuentes"
            };
        }
        if (so.contains("mac")) {
            return new String[]{
                "/Library/Fonts", "/System/Library/Fonts", home + "/Library/Fonts",
                "/Bingo/Fuentes", "/Bingo/fuentes"
            };
        }
        // Linux / Unix
        return new String[]{
            "/usr/share/fonts", "/usr/local/share/fonts", home + "/.fonts",
            home + "/.local/share/fonts",
            "/Bingo/Fuentes", "/Bingo/fuentes"
        };
    }

    @SuppressWarnings("unchecked")
    private static Set<String> lower(Set raw) {
        Set<String> s = new TreeSet<>();
        for (Object o : raw) if (o != null) s.add(o.toString().toLowerCase());
        return s;
    }

    private static String tituloCase(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        boolean nuevo = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c) || c == '-') { nuevo = true; sb.append(c); }
            else if (nuevo) { sb.append(Character.toUpperCase(c)); nuevo = false; }
            else sb.append(c);
        }
        return sb.toString();
    }
}
