package Controlador;

import Modelo.ConfigReporte;
import Modelo.EstiloTexto;
import Modelo.Tabla;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfWriter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GeneradorPDF {

    private static final float PAGE_W  = 612f;
    private static final float MARGIN  = 18f;
    private static final float COL_GAP = 12f;
    private static final float ROW_GAP = 9f;

    // Colores del chip redondeado de la boleta (gris suave)
    private static final Color CHIP_FILL   = new Color(235, 235, 235);
    private static final Color CHIP_BORDER = new Color(170, 170, 170);

    // =====================================================================
    // Layout dinámico según tamaño de página
    // =====================================================================

    private static class Layout {
        final float pageH, tW, tH, hdrH, stripH, gridH, cellH, cellW, revTopH, revStripH;

        Layout(int tamañoPagina) {
            pageH     = (tamañoPagina == ConfigReporte.PAGINA_CARTA) ? 792f : 1008f;
            tW        = (PAGE_W - 2 * MARGIN - COL_GAP) / 2f;          // 282
            tH        = (pageH  - 2 * MARGIN - 2 * ROW_GAP) / 3f;
            hdrH      = 46f;
            stripH    = (tamañoPagina == ConfigReporte.PAGINA_CARTA) ? 28f : 36f;
            gridH     = tH - hdrH - stripH;
            cellH     = gridH / 5f;
            cellW     = tW / 5f;
            revTopH   = 44f;
            revStripH = (tamañoPagina == ConfigReporte.PAGINA_CARTA) ? 46f : 58f;
        }

        float[][] posFrente() {
            float[][] pos = new float[6][2];
            int idx = 0;
            for (int row = 2; row >= 0; row--)
                for (int col = 0; col < 2; col++) {
                    pos[idx][0] = MARGIN + col * (tW + COL_GAP);
                    pos[idx][1] = MARGIN + row * (tH + ROW_GAP);
                    idx++;
                }
            return pos;
        }

        float[][] posReverso() {
            float[][] pos = new float[6][2];
            int idx = 0;
            for (int row = 2; row >= 0; row--)
                for (int col = 1; col >= 0; col--) {
                    pos[idx][0] = MARGIN + col * (tW + COL_GAP);
                    pos[idx][1] = MARGIN + row * (tH + ROW_GAP);
                    idx++;
                }
            return pos;
        }
    }

    // =====================================================================
    // API pública
    // =====================================================================

    public static void generarFrente(List<Tabla> tablas, ConfigReporte cfg, OutputStream out)
            throws Exception { doGenerate(tablas, cfg, out, ConfigReporte.MODO_FRENTE); }

    public static void generarReverso(List<Tabla> tablas, ConfigReporte cfg, OutputStream out)
            throws Exception { doGenerate(tablas, cfg, out, ConfigReporte.MODO_REVERSO); }

    public static void generarDobleCara(List<Tabla> tablas, ConfigReporte cfg, OutputStream out)
            throws Exception { doGenerate(tablas, cfg, out, ConfigReporte.MODO_DOBLE_CARA); }

    public static void generarDosPDF(List<Tabla> tablas, ConfigReporte cfg, String pathBase)
            throws Exception {
        try (FileOutputStream f = new FileOutputStream(pathBase + "_Frentes.pdf");
             FileOutputStream r = new FileOutputStream(pathBase + "_Reversos.pdf")) {
            doGenerate(tablas, cfg, f, ConfigReporte.MODO_FRENTE);
            doGenerate(tablas, cfg, r, ConfigReporte.MODO_REVERSO);
        }
    }

    // =====================================================================
    // Motor central
    // =====================================================================

    private static void doGenerate(List<Tabla> tablas, ConfigReporte cfg,
                                    OutputStream out, int modo) throws Exception {
        Layout lay = new Layout(cfg.tamañoPagina);
        Document doc = new Document(
                new com.lowagie.text.Rectangle(PAGE_W, lay.pageH), 0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        Image wmImage = (cfg.rutaMarcaAgua != null && !cfg.rutaMarcaAgua.isEmpty())
                ? Image.getInstance(cfg.rutaMarcaAgua) : null;

        int pages = Math.max(1, (int) Math.ceil(tablas.size() / 6.0));
        for (int pg = 0; pg < pages; pg++) {
            if (pg > 0) doc.newPage();
            List<Tabla> sub = tablas.subList(pg * 6, Math.min(pg * 6 + 6, tablas.size()));

            switch (modo) {
                case ConfigReporte.MODO_DOBLE_CARA:
                    renderPagina(writer, sub, cfg, lay, wmImage, true);
                    doc.newPage();
                    renderPagina(writer, sub, cfg, lay, wmImage, false);
                    break;
                case ConfigReporte.MODO_FRENTE:
                    renderPagina(writer, sub, cfg, lay, wmImage, true);
                    break;
                default:
                    renderPagina(writer, sub, cfg, lay, wmImage, false);
            }
        }
        doc.close();
    }

    private static void renderPagina(PdfWriter writer, List<Tabla> tablas,
                                      ConfigReporte cfg, Layout lay,
                                      Image wmImage, boolean esFrente) throws Exception {
        PdfContentByte cb = writer.getDirectContent();
        float[][] pos = esFrente ? lay.posFrente() : lay.posReverso();
        for (int i = 0; i < tablas.size() && i < pos.length; i++) {
            if (esFrente)
                dibujarFrente(cb, writer, tablas.get(i), pos[i][0], pos[i][1], cfg, lay, wmImage);
            else
                dibujarReverso(cb, writer, tablas.get(i), pos[i][0], pos[i][1], cfg, lay, wmImage);
        }
    }

    // =====================================================================
    // Dibujo del frente
    // =====================================================================

    private static void dibujarFrente(PdfContentByte cb, PdfWriter writer,
                                       Tabla t, float x, float y,
                                       ConfigReporte cfg, Layout lay,
                                       Image wmImage) throws Exception {
        GestorFuentes gf = GestorFuentes.get();
        BaseFont bfTit = gf.fontPDF(cfg.estiloTitulo).getBaseFont();
        BaseFont bfNum = gf.fontPDF(cfg.estiloNumeros).getBaseFont();
        BaseFont bfBol = gf.fontPDF(cfg.estiloBoleta).getBaseFont();

        // Marca de agua
        if (wmImage != null && (cfg.marcaAguaDestino == ConfigReporte.DEST_FRENTE
                || cfg.marcaAguaDestino == ConfigReporte.DEST_AMBOS))
            dibujarMarcaAgua(writer.getDirectContentUnder(), wmImage, x, y, lay.tW, lay.tH,
                    cfg.marcaAguaOpacidad);

        // Marco exterior
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco);
            cb.rectangle(x, y, lay.tW, lay.tH);
            cb.stroke();
            cb.restoreState();
        }

        // ---- Cabecera (letras del título) ----
        float hdrBase = y + lay.stripH + lay.gridH;
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco);
            cb.moveTo(x, hdrBase); cb.lineTo(x + lay.tW, hdrBase);
            cb.stroke();
            cb.restoreState();
        }
        String tit = normalizarTitulo(cfg.titulo);
        float titSize = cfg.estiloTitulo.tamaño;
        // Banda continua de resaltado detrás de todo el título (si tiene fondo)
        if (cfg.estiloTitulo.tieneFondo()) {
            cb.saveState();
            cb.setColorFill(cfg.estiloTitulo.fondo);
            cb.rectangle(x + 1f, hdrBase + 1f, lay.tW - 2f, lay.hdrH - 2f);
            cb.fill();
            cb.restoreState();
        }
        for (int col = 0; col < 5; col++) {
            float cy = hdrBase + (lay.hdrH - titSize) / 2f + 2f;
            drawText(cb, cfg.estiloTitulo, bfTit,
                    x + col * lay.cellW + lay.cellW / 2f, cy,
                    PdfContentByte.ALIGN_CENTER, String.valueOf(tit.charAt(col)), false);
        }

        // ---- Líneas de la grilla ----
        float gridBase = y + lay.stripH;
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco * 0.75f);
            for (int row = 1; row < 5; row++) {
                float ly2 = gridBase + row * lay.cellH;
                cb.moveTo(x, ly2); cb.lineTo(x + lay.tW, ly2);
            }
            cb.moveTo(x, gridBase); cb.lineTo(x + lay.tW, gridBase);
            for (int col = 1; col < 5; col++) {
                float lx = x + col * lay.cellW;
                cb.moveTo(lx, gridBase); cb.lineTo(lx, gridBase + lay.gridH);
            }
            cb.stroke();
            cb.restoreState();
        }

        // ---- Números ----
        int[] nums = numeros(t);
        float numSize = cfg.estiloNumeros.tamaño;
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 5; row++) {
                if (col == 2 && row == 2) continue;
                float nx = x + col * lay.cellW + lay.cellW / 2f;
                float ny = gridBase + (4 - row) * lay.cellH
                        + (lay.cellH - numSize) / 2f + 1f;
                drawText(cb, cfg.estiloNumeros, bfNum, nx, ny,
                        PdfContentByte.ALIGN_CENTER, String.valueOf(nums[col * 5 + row]), false);
            }
        }

        // ---- Celda central: QR (si habilitado para frente) o número de boleta ----
        float cX = x + 2 * lay.cellW;
        float cY = gridBase + 2 * lay.cellH;
        boolean qrOF = cfg.generarQR;   // QR de Seguridad: siempre en el frente
        if (qrOF) {
            float qrSize = Math.min(lay.cellW, lay.cellH) - 6f;
            Image qrImg = generarQR(ConsultaTablas.buildQRContent(t), 200, cfg.estiloQR);
            qrImg.scaleAbsolute(qrSize, qrSize);
            qrImg.setAbsolutePosition(
                    cX + (lay.cellW - qrSize) / 2f,
                    cY + (lay.cellH - qrSize) / 2f);
            cb.addImage(qrImg);
        } else {
            cb.saveState();
            cb.setColorFill(new Color(230, 230, 230));
            cb.ellipse(cX + 4f, cY + 3f, cX + lay.cellW - 4f, cY + lay.cellH - 3f);
            cb.fill();
            cb.restoreState();
            drawText(cb, cfg.estiloBoleta, bfBol,
                    cX + lay.cellW / 2f, cY + lay.cellH / 2f - cfg.estiloBoleta.tamaño * 0.35f,
                    PdfContentByte.ALIGN_CENTER, formatBoleta(t.getNumTabla(), cfg.digitosBoleta), false);
        }

        // ---- Franja inferior: fecha, valor, caducidad (según destino) ----
        boolean fechaOF = enCara(cfg.fechaDestino, true);
        boolean valorOF = enCara(cfg.valorDestino, true);
        boolean cadOF   = enCara(cfg.caducidadDestino, true);

        float chipW = chipAncho(bfBol, cfg.estiloBoleta,
                formatBoleta(t.getNumTabla(), cfg.digitosBoleta));
        float availW = lay.tW - chipW - 14f;

        List<String[]>  items = new ArrayList<>();  // {texto}
        List<EstiloTexto> ests = new ArrayList<>();
        if (valorOF) { items.add(new String[]{"Val: " + cfg.valor});      ests.add(cfg.estiloValor); }
        if (fechaOF) { items.add(new String[]{cfg.fechaJuego});           ests.add(cfg.estiloFecha); }
        if (cadOF)   { items.add(new String[]{"Cad. " + cfg.caducidad + "d"}); ests.add(cfg.estiloValor); }

        if (!items.isEmpty()) {
            float itemW = availW / items.size();
            for (int i = 0; i < items.size(); i++) {
                float ix = x + 2f + i * itemW + itemW / 2f;
                drawText(cb, ests.get(i), gf.fontPDF(ests.get(i)).getBaseFont(),
                        ix, y + lay.stripH / 2f - 3f,
                        PdfContentByte.ALIGN_CENTER, items.get(i)[0], false);
            }
        }

        // ---- Chip redondeado con el número de boleta (derecha de la franja) ----
        float chipCx = x + lay.tW - chipW / 2f - 4f;
        float chipCy = y + lay.stripH / 2f;
        dibujarChipBoleta(cb, bfBol, cfg.estiloBoleta,
                formatBoleta(t.getNumTabla(), cfg.digitosBoleta), chipCx, chipCy, chipW);
        // El QR Ganador va únicamente en el reverso (ver dibujarReverso).
    }

    // =====================================================================
    // Dibujo del reverso
    // =====================================================================

    private static void dibujarReverso(PdfContentByte cb, PdfWriter writer,
                                        Tabla t, float x, float y,
                                        ConfigReporte cfg, Layout lay,
                                        Image wmImage) throws Exception {
        GestorFuentes gf = GestorFuentes.get();
        BaseFont bfTit = gf.fontPDF(cfg.estiloTitulo).getBaseFont();
        BaseFont bfBol = gf.fontPDF(cfg.estiloBoleta).getBaseFont();

        if (wmImage != null && (cfg.marcaAguaDestino == ConfigReporte.DEST_REVERSO
                || cfg.marcaAguaDestino == ConfigReporte.DEST_AMBOS))
            dibujarMarcaAgua(writer.getDirectContentUnder(), wmImage, x, y, lay.tW, lay.tH,
                    cfg.marcaAguaOpacidad);

        // Marco exterior
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco);
            cb.rectangle(x, y, lay.tW, lay.tH);
            cb.stroke();
            cb.restoreState();
        }

        // ---- Título reverso ----
        float hdrLineY = y + lay.tH - lay.revTopH;
        if (cfg.estiloTitulo.tieneFondo()) {
            cb.saveState();
            cb.setColorFill(cfg.estiloTitulo.fondo);
            cb.rectangle(x + 1f, hdrLineY + 1f, lay.tW - 2f, lay.revTopH - 2f);
            cb.fill();
            cb.restoreState();
        }
        drawText(cb, cfg.estiloTitulo, bfTit, x + lay.tW / 2f,
                y + lay.tH - (lay.revTopH + cfg.estiloTitulo.tamaño) / 2f,
                PdfContentByte.ALIGN_CENTER, cfg.tituloReverso, false);
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco);
            cb.moveTo(x, hdrLineY); cb.lineTo(x + lay.tW, hdrLineY);
            cb.stroke();
            cb.restoreState();
        }

        // ---- Franja inferior: valor, caducidad, fecha, QR ----
        float stripTopY = y + lay.revStripH;
        if (cfg.imprimirMarco) {
            cb.saveState();
            cb.setColorStroke(cfg.colorMarco);
            cb.setLineWidth(cfg.anchoMarco);
            cb.moveTo(x, stripTopY); cb.lineTo(x + lay.tW, stripTopY);
            cb.stroke();
            cb.restoreState();
        }

        boolean valorOR = enCara(cfg.valorDestino, false);
        boolean cadOR   = enCara(cfg.caducidadDestino, false);
        boolean fechaOR = enCara(cfg.fechaDestino, false);

        BaseFont bfVal = gf.fontPDF(cfg.estiloValor).getBaseFont();
        float lineY = y + lay.revStripH - 13f;
        if (valorOR) {
            drawText(cb, cfg.estiloValor, bfVal, x + 5f, lineY,
                    PdfContentByte.ALIGN_LEFT, "Valor: " + cfg.valor, false);
            lineY -= 13f;
        }
        if (cadOR) {
            drawText(cb, cfg.estiloValor, bfVal, x + 5f, lineY,
                    PdfContentByte.ALIGN_LEFT, "Caducidad " + cfg.caducidad + " días", false);
            lineY -= 13f;
        }
        if (fechaOR) {
            drawText(cb, cfg.estiloFecha, gf.fontPDF(cfg.estiloFecha).getBaseFont(),
                    x + lay.tW / 2f, y + 4f, PdfContentByte.ALIGN_CENTER, cfg.fechaJuego, false);
        }

        // QR Ganador del reverso: en TODAS las tablas (premiadas con su mensaje,
        // las demás con el mensaje por defecto). Esquina inferior derecha.
        if (cfg.generarQRGanador) {
            float qs = lay.revStripH - 6f;
            Image qg = generarQR(cfg.contenidoQRGanador(t.getNumTabla()), 200, cfg.estiloQR);
            qg.scaleAbsolute(qs, qs);
            qg.setAbsolutePosition(x + lay.tW - qs - 3f, y + 3f);
            cb.addImage(qg);
        }

        // Chip de boleta (zona inferior izquierda, libre del QR)
        float chipW  = chipAncho(bfBol, cfg.estiloBoleta,
                formatBoleta(t.getNumTabla(), cfg.digitosBoleta));
        float chipCx = x + chipW / 2f + 6f;
        float chipCy = y + 11f;
        dibujarChipBoleta(cb, bfBol, cfg.estiloBoleta,
                formatBoleta(t.getNumTabla(), cfg.digitosBoleta), chipCx, chipCy, chipW);

        // ---- Área de texto configurable ----
        Font lowagie = gf.fontPDF(cfg.estiloTextoReverso);
        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(
                x + 6f, y + lay.revStripH + 3f,
                x + lay.tW - 6f, y + lay.tH - lay.revTopH - 3f);
        for (String linea : cfg.textoReverso.split("\n")) {
            Paragraph p = new Paragraph(linea.isEmpty() ? " " : linea, lowagie);
            p.setSpacingBefore(1.5f);
            ct.addElement(p);
        }
        ct.go();
    }

    // =====================================================================
    // Helper central de dibujo de texto
    //   - resaltado de fondo (estilo.fondo)
    //   - negrita sintética (si la cara resuelta no es bold)
    //   - cursiva sintética (skew, si la cara resuelta no es itálica)
    // =====================================================================

    private static void drawText(PdfContentByte cb, EstiloTexto e, BaseFont bf,
                                 float x, float y, int align, String texto, boolean pintarFondo) {
        if (texto == null) texto = "";
        float size = e.tamaño;
        float w = bf.getWidthPoint(texto, size);
        float x0;
        switch (align) {
            case PdfContentByte.ALIGN_RIGHT:  x0 = x - w;     break;
            case PdfContentByte.ALIGN_CENTER: x0 = x - w / 2; break;
            default:                          x0 = x;         break;
        }

        // Resaltado de fondo
        if (pintarFondo && e.tieneFondo()) {
            float pad = size * 0.12f;
            float yb  = y - size * 0.24f;
            float yt  = y + size * 0.82f;
            cb.saveState();
            cb.setColorFill(e.fondo);
            cb.rectangle(x0 - pad, yb, w + 2 * pad, yt - yb);
            cb.fill();
            cb.restoreState();
        }

        cb.saveState();
        cb.setColorFill(e.color);

        boolean caraBold = nombreEsBold(bf.getPostscriptFontName());
        if (e.negrita && !caraBold) {
            cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
            cb.setLineWidth(size * 0.025f);
            cb.setColorStroke(e.color);
        }

        float italicAngle = bf.getFontDescriptor(BaseFont.ITALICANGLE, 1f);
        float shear = (e.cursiva && Math.abs(italicAngle) < 1f) ? 0.21f : 0f;

        cb.beginText();
        cb.setFontAndSize(bf, size);
        cb.setTextMatrix(1, 0, shear, 1, x0, y);
        cb.showText(texto);
        cb.endText();
        cb.restoreState();
    }

    // =====================================================================
    // Utilidades de boleta / chip
    // =====================================================================

    private static String formatBoleta(int num, int digitos) {
        if (digitos < 1) digitos = 4;
        return String.format("%0" + digitos + "d", num);
    }

    private static float chipAncho(BaseFont bf, EstiloTexto e, String texto) {
        return bf.getWidthPoint(texto, e.tamaño) + 12f;
    }

    /** Dibuja el número de boleta dentro de un recuadro redondeado gris suave. */
    private static void dibujarChipBoleta(PdfContentByte cb, BaseFont bf, EstiloTexto e,
                                          String texto, float cx, float cy, float chipW) {
        float chipH = e.tamaño + 7f;
        float rx = cx - chipW / 2f;
        float ry = cy - chipH / 2f;
        float radio = chipH / 2.5f;

        cb.saveState();
        cb.setColorFill(CHIP_FILL);
        cb.setColorStroke(CHIP_BORDER);
        cb.setLineWidth(0.7f);
        cb.roundRectangle(rx, ry, chipW, chipH, radio);
        cb.fillStroke();
        cb.restoreState();

        // El texto del chip nunca pinta su propio resaltado (el fondo es el chip).
        drawText(cb, e, bf, cx, cy - e.tamaño * 0.33f, PdfContentByte.ALIGN_CENTER, texto, false);
    }

    // =====================================================================
    // Utilidades varias
    // =====================================================================

    private static boolean enCara(int destino, boolean frente) {
        int propio = frente ? ConfigReporte.DEST_FRENTE : ConfigReporte.DEST_REVERSO;
        return destino == propio || destino == ConfigReporte.DEST_AMBOS;
    }

    private static boolean nombreEsBold(String nombre) {
        if (nombre == null) return false;
        String n = nombre.toLowerCase();
        return n.contains("bold") || n.contains("black") || n.contains("heavy")
                || n.contains("semibold") || n.contains("demi");
    }

    private static void dibujarMarcaAgua(PdfContentByte cbUnder, Image img,
                                          float x, float y, float w, float h,
                                          float opacidad) throws Exception {
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(Math.max(0f, Math.min(1f, opacidad)));
        cbUnder.saveState();
        cbUnder.setGState(gs);
        float scale = Math.min(w / img.getPlainWidth(), h / img.getPlainHeight()) * 0.75f;
        float iw = img.getPlainWidth() * scale;
        float ih = img.getPlainHeight() * scale;
        img.scaleAbsolute(iw, ih);
        img.setAbsolutePosition(x + (w - iw) / 2f, y + (h - ih) / 2f);
        cbUnder.addImage(img);
        cbUnder.restoreState();
    }

    private static Image generarQR(String contenido, int px, EstiloTexto estiloQR) throws Exception {
        int fg = (estiloQR != null && estiloQR.color != null) ? estiloQR.color.getRGB() : 0xFF000000;
        int bg = (estiloQR != null && estiloQR.fondo != null) ? estiloQR.fondo.getRGB() : 0xFFFFFFFF;
        QRCodeWriter qrw = new QRCodeWriter();
        BitMatrix mx = qrw.encode(contenido, BarcodeFormat.QR_CODE, px, px);
        BufferedImage bi = new BufferedImage(px, px, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < px; i++)
            for (int j = 0; j < px; j++)
                bi.setRGB(i, j, mx.get(i, j) ? fg : bg);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "PNG", baos);
        return Image.getInstance(baos.toByteArray());
    }

    private static String normalizarTitulo(String t) {
        if (t == null || t.isEmpty()) return "BINGO";
        t = t.toUpperCase();
        if (t.length() > 5) return t.substring(0, 5);
        while (t.length() < 5) t += " ";
        return t;
    }

    private static int[] numeros(Tabla t) {
        return new int[]{
            t.getN1(),  t.getN2(),  t.getN3(),  t.getN4(),  t.getN5(),
            t.getN6(),  t.getN7(),  t.getN8(),  t.getN9(),  t.getN10(),
            t.getN11(), t.getN12(), t.getN13(), t.getN14(), t.getN15(),
            t.getN16(), t.getN17(), t.getN18(), t.getN19(), t.getN20(),
            t.getN21(), t.getN22(), t.getN23(), t.getN24(), t.getN25()
        };
    }
}
