package Modelo;

import com.lowagie.text.Font;
import java.awt.Color;

/**
 * Estilo de formato reutilizable para un elemento de texto del reporte:
 * fuente, negrita, cursiva, tamaño, color de letra y color de fondo (resaltado).
 *
 * Un {@code fondo == null} significa "sin resaltado".
 */
public class EstiloTexto {

    public String  fuente;
    public boolean negrita;
    public boolean cursiva;
    public float   tamaño;
    public Color   color;
    public Color   fondo;   // null = sin resaltado

    public EstiloTexto() {
        this("Helvetica", false, false, 12f, Color.BLACK, null);
    }

    public EstiloTexto(String fuente, boolean negrita, boolean cursiva,
                       float tamaño, Color color, Color fondo) {
        this.fuente  = fuente;
        this.negrita = negrita;
        this.cursiva = cursiva;
        this.tamaño  = tamaño;
        this.color   = color;
        this.fondo   = fondo;
    }

    /** Combina las banderas de estilo de OpenPDF (NORMAL/BOLD/ITALIC/BOLDITALIC). */
    public int styleFlags() {
        int s = Font.NORMAL;
        if (negrita) s |= Font.BOLD;
        if (cursiva) s |= Font.ITALIC;
        return s;
    }

    public boolean tieneFondo() {
        return fondo != null;
    }
}
