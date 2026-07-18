package Vista;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Ventana "flash" de anuncio del ganador ACTUAL (4 segundos, animada y MODAL):
 * fondo con degradado que cambia de tono, rayos giratorios, confeti cayendo,
 * texto "¡GANADOR!" con brillo pulsante, el nombre de la figura, el patrón 5x5
 * resplandeciendo y la(s) tabla(s) ganadora(s). Aparece PRIMERO; al terminar
 * hace fade-out y {@link #iniciar()} retorna para dar paso a la ventana de
 * historial (que entra con fade-in) = transición entre ventanas.
 */
public class FlashGanador extends javax.swing.JDialog {

    private static final int DURACION_MS = 4000;

    private final String figura;
    private final List<String> tablas;
    private final boolean[] patron;   // 25 celdas (indice = fila + 5*col)

    private long inicioNanos;
    private javax.swing.Timer timer;
    private final Confeti[] confeti;
    private boolean translucido = true;

    public FlashGanador(Window owner, String figura, List<String> tablas, boolean[] patron) {
        super(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.figura = (figura == null || figura.isEmpty()) ? "GANADOR" : figura;
        this.tablas = tablas;
        this.patron = (patron != null && patron.length == 25) ? patron : new boolean[25];

        setUndecorated(true);
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        int w = 1000, h = 720;
        setSize(w, h);
        setLocationRelativeTo(owner);

        confeti = new Confeti[110];
        Random r = new Random(System.nanoTime());
        for (int i = 0; i < confeti.length; i++) confeti[i] = new Confeti(w, h, r);

        setContentPane(new Lienzo());
    }

    /** Muestra la ventana (MODAL): bloquea 4s animando y luego se cierra. */
    public void iniciar() {
        try { setOpacity(0f); } catch (Throwable t) { translucido = false; }
        inicioNanos = System.nanoTime();
        timer = new javax.swing.Timer(16, e -> tick());
        timer.setCoalesce(true);
        timer.start();
        setVisible(true);   // bloquea hasta dispose() (bucle modal ejecuta el timer)
    }

    private void tick() {
        long ms = (System.nanoTime() - inicioNanos) / 1_000_000L;
        float p = Math.min(1f, ms / (float) DURACION_MS);
        if (translucido) {
            float op = (p < 0.12f) ? (p / 0.12f) : (p > 0.82f ? (1f - p) / 0.18f : 1f);
            try { setOpacity(Math.max(0f, Math.min(1f, op))); } catch (Throwable ignore) { }
        }
        for (Confeti c : confeti) c.update();
        getContentPane().repaint();
        if (ms >= DURACION_MS) {
            timer.stop();
            dispose();
        }
    }

    // ================= Lienzo animado =================
    private class Lienzo extends javax.swing.JComponent {
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            float ph = (System.nanoTime() - inicioNanos) / 1_000_000_000f;

            float hue = (0.58f + ph * 0.12f) % 1f;
            Color c1 = Color.getHSBColor(hue, 0.75f, 0.22f);
            g.setPaint(new RadialGradientPaint(new Point(w / 2, h / 2), Math.max(w, h) / 1.15f,
                    new float[]{0f, 1f}, new Color[]{c1, Color.BLACK}));
            g.fillRect(0, 0, w, h);

            dibujarRayos(g, w, h, ph);
            for (Confeti c : confeti) c.dibujar(g);

            float pulso = 1f + 0.07f * (float) Math.sin(ph * 6.0);
            textoBrillo(g, "¡GANADOR!", w / 2, (int) (h * 0.17), (int) (78 * pulso),
                    new Color(255, 210, 40), true);
            textoBrillo(g, figura, w / 2, (int) (h * 0.34), 60, new Color(40, 255, 90), true);

            dibujarPatron(g, w / 2, (int) (h * 0.58), 230, ph);

            String tabs;
            if (tablas == null || tablas.isEmpty()) tabs = "";
            else if (tablas.size() == 1) tabs = "Tabla No. " + tablas.get(0);
            else tabs = tablas.size() + " tablas ganadoras";
            if (!tabs.isEmpty())
                textoBrillo(g, tabs, w / 2, (int) (h * 0.90), 46, Color.WHITE, false);

            g.dispose();
        }
    }

    private void dibujarRayos(Graphics2D g0, int w, int h, float ph) {
        Graphics2D g = (Graphics2D) g0.create();
        g.translate(w / 2, h / 2);
        g.rotate(ph * 0.35);
        int n = 16, R = Math.max(w, h);
        for (int i = 0; i < n; i++) {
            g.rotate(2 * Math.PI / n);
            g.setColor(new Color(255, 255, 255, (i % 2 == 0) ? 16 : 7));
            g.fillPolygon(new int[]{0, R, R}, new int[]{0, -70, 70}, 3);
        }
        g.dispose();
    }

    private void textoBrillo(Graphics2D g, String s, int cx, int cy, int size, Color color, boolean bold) {
        g.setFont(new Font("Dialog", bold ? Font.BOLD : Font.PLAIN, size));
        FontMetrics fm = g.getFontMetrics();
        int x = cx - fm.stringWidth(s) / 2, y = cy;
        for (int i = 5; i >= 1; i--) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            for (int a = 0; a < 8; a++) {
                double ang = a * Math.PI / 4;
                g.drawString(s, x + (int) (Math.cos(ang) * i), y + (int) (Math.sin(ang) * i));
            }
        }
        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(s, x + 2, y + 2);
        g.setColor(color);
        g.drawString(s, x, y);
    }

    private void dibujarPatron(Graphics2D g, int cx, int cy, int total, float ph) {
        int cell = total / 5, x0 = cx - total / 2, y0 = cy - total / 2;
        float glow = 0.55f + 0.45f * (float) Math.sin(ph * 5.0);
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(x0 - 10, y0 - 10, total + 20, total + 20, 18, 18);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int idx = r + 5 * c;
                int px = x0 + c * cell, py = y0 + r * cell;
                boolean on = idx < 25 && patron[idx];
                if (on) {
                    g.setColor(new Color(255, 215, 0, (int) (150 * glow)));
                    g.fillRoundRect(px - 3, py - 3, cell + 4, cell + 4, 12, 12);
                    g.setColor(new Color(255, 232, 90));
                    g.fillRoundRect(px + 3, py + 3, cell - 6, cell - 6, 9, 9);
                } else {
                    g.setColor(new Color(55, 58, 70));
                    g.fillRoundRect(px + 3, py + 3, cell - 6, cell - 6, 9, 9);
                }
            }
        }
    }

    // ================= Confeti =================
    private static class Confeti {
        float x, y, vx, vy, rot, vrot, size;
        Color color;
        int W, H;
        Confeti(int w, int h, Random r) { W = w; H = h; reset(r); y = -r.nextInt(h); }
        final void reset(Random r) {
            x = r.nextInt(Math.max(1, W));
            y = -r.nextInt(60) - 10;
            vx = (r.nextFloat() - 0.5f) * 2.4f;
            vy = 2.2f + r.nextFloat() * 4.5f;
            rot = r.nextFloat() * 6.28f;
            vrot = (r.nextFloat() - 0.5f) * 0.35f;
            size = 8 + r.nextInt(12);
            Color[] cs = {
                new Color(255, 215, 0), new Color(40, 255, 90), new Color(255, 80, 90),
                new Color(90, 170, 255), new Color(255, 255, 255), new Color(255, 140, 0),
                new Color(200, 90, 255)
            };
            color = cs[r.nextInt(cs.length)];
        }
        void update() {
            x += vx; y += vy; rot += vrot; vy += 0.02f;
            if (y > H + 20) { y = -20; vy = 2.2f + (float) (Math.random() * 4.5); x = (float) (Math.random() * W); }
        }
        void dibujar(Graphics2D g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.translate(x, y);
            g.rotate(rot);
            g.setColor(color);
            g.fillRect((int) (-size / 2), (int) (-size / 2), (int) size, (int) (size * 0.6f));
            g.dispose();
        }
    }
}
