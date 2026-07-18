package Modelo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ConfigReporte {

    // ---- Destinos ----
    public static final int DEST_FRENTE  = 0;
    public static final int DEST_REVERSO = 1;
    public static final int DEST_AMBOS   = 2;

    // ---- Modos de salida ----
    public static final int MODO_FRENTE     = 0;
    public static final int MODO_REVERSO    = 1;
    public static final int MODO_DOBLE_CARA = 2;
    public static final int MODO_DOS_PDF    = 3;

    // ---- Tamaño de página ----
    public static final int PAGINA_CARTA  = 0;   // 612 × 792 pt
    public static final int PAGINA_OFICIO = 1;   // 612 × 1008 pt
    public int tamañoPagina = PAGINA_OFICIO;

    // ---- Estilos de texto independientes por elemento ----
    public EstiloTexto estiloTitulo  =
            new EstiloTexto("Helvetica", true,  false, 28f, Color.RED,       null);
    public EstiloTexto estiloNumeros =
            new EstiloTexto("Helvetica", true,  false, 18f, Color.BLACK,     null);
    public EstiloTexto estiloBoleta  =
            new EstiloTexto("Helvetica", true,  false, 12f, Color.BLACK,     null);
    public EstiloTexto estiloTextoReverso =
            new EstiloTexto("Helvetica", false, false, 7.5f, Color.BLACK,    null);
    public EstiloTexto estiloFecha   =
            new EstiloTexto("Helvetica", false, false, 7f,  Color.DARK_GRAY, null);
    public EstiloTexto estiloValor   =
            new EstiloTexto("Helvetica", false, false, 7f,  Color.DARK_GRAY, null);
    // QR: 'fuente' se ignora; se usan color (módulos), fondo (fondo del QR) y tamaño.
    public EstiloTexto estiloQR =
            new EstiloTexto("Helvetica", false, false, 100f, Color.BLACK,    Color.WHITE);

    // ---- Frente ----
    public String  titulo         = "BINGO";
    public Color   colorMarco     = Color.BLACK;
    public float   anchoMarco     = 1.5f;
    public boolean imprimirMarco  = true;

    /** Número de cifras del número de boleta (4 → 0000, 5 → 00000). */
    public int digitosBoleta = 4;

    // ---- Datos comunes ----
    public String fechaJuego      = "DD/MM/YYYY";
    public int    fechaDestino    = DEST_FRENTE;

    public String valor           = "$ 1.000";
    public int    valorDestino    = DEST_REVERSO;

    public String caducidad       = "15";
    public int    caducidadDestino = DEST_REVERSO;

    // ---- Reverso ----
    public String tituloReverso = "BINGO";
    public String textoReverso  =
        "1. Cartón que presente alteraciones, rayones o tachones será anulado automáticamente.\n\n" +
        "2. El premio se pagará únicamente al portador del cartón, previa verificación de su " +
        "nombre en la planilla de control.\n\n" +
        "3. Si el sistema registra más de un cartón ganador, el premio se repartirá por partes iguales.\n\n" +
        "4. El sorteo podrá ser aplazado o cancelado por motivos de fuerza mayor.\n\n" +
        "5. Todos los Derechos Reservados.";

    // ---- QR de Seguridad (siempre en el FRENTE) ----
    public boolean generarQR = false;

    // ---- QR Ganador (siempre en el REVERSO; en TODAS las tablas) ----
    public boolean generarQRGanador  = false;
    public List<RangoPremio> premios = new ArrayList<>();
    /** Mensaje del QR Ganador para las tablas SIN premio configurado. */
    public String mensajePremioDefecto = "Hoy no tuviste suerte, sigue intentando";

    // ---- Marca de agua ----
    public String rutaMarcaAgua    = null;
    public int    marcaAguaDestino = DEST_AMBOS;
    /** Opacidad de la marca de agua, 0.0 (invisible) – 1.0 (opaca). */
    public float  marcaAguaOpacidad = 0.15f;

    /** Devuelve el primer rango de premio que contiene el cartón, o null. */
    public RangoPremio premioDe(int numTabla) {
        for (RangoPremio r : premios)
            if (r.contiene(numTabla)) return r;
        return null;
    }

    /**
     * Contenido del QR Ganador para un cartón: si está premiado usa el mensaje del
     * rango; si no, usa el mensaje por defecto (de consuelo).
     */
    public String contenidoQRGanador(int numTabla) {
        RangoPremio r = premioDe(numTabla);
        if (r != null) return r.contenidoQR(numTabla);
        return "GANADOR|" + numTabla + "|" + mensajePremioDefecto + "|";
    }
}
