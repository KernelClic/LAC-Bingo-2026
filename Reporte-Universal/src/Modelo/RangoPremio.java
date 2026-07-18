package Modelo;

/**
 * Un rango de cartones premiados para el "QR Ganador".
 * Los cartones cuyo número esté entre {@code desde} y {@code hasta} (inclusive)
 * reciben un QR adicional que referencia el {@code mensaje} del premio.
 *
 * Ejemplo: desde=10, hasta=12, mensaje="Gana Moto"  → cartones 10, 11 y 12.
 *          desde=234, hasta=234, mensaje="Gana Carro" → solo el cartón 234.
 */
public class RangoPremio {

    private int    desde;
    private int    hasta;
    private String mensaje;
    private String fechaCreacion;

    public RangoPremio() {
    }

    public RangoPremio(int desde, int hasta, String mensaje, String fechaCreacion) {
        this.desde         = desde;
        this.hasta         = hasta;
        this.mensaje       = mensaje;
        this.fechaCreacion = fechaCreacion;
    }

    /** ¿El cartón {@code numTabla} pertenece a este rango premiado? */
    public boolean contiene(int numTabla) {
        return numTabla >= desde && numTabla <= hasta;
    }

    /** Contenido a codificar en el QR Ganador. */
    public String contenidoQR(int numTabla) {
        return "GANADOR|" + numTabla + "|" + mensaje + "|" + fechaCreacion;
    }

    public int    getDesde()         { return desde; }
    public int    getHasta()         { return hasta; }
    public String getMensaje()       { return mensaje; }
    public String getFechaCreacion() { return fechaCreacion; }

    public void setDesde(int desde)                 { this.desde = desde; }
    public void setHasta(int hasta)                 { this.hasta = hasta; }
    public void setMensaje(String mensaje)          { this.mensaje = mensaje; }
    public void setFechaCreacion(String fecha)      { this.fechaCreacion = fecha; }
}
