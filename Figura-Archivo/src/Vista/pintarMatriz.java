
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import javax.swing.JPanel;

class MatrixPanel extends JPanel {

    private static final int MATRIX_SIZE = 5;
    private static final int SQUARE_SIZE = 50;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Obtener el contexto gráfico 2D
        Graphics2D g2d = (Graphics2D) g;

        // Matriz de colores aleatorios
        Color[][] matrix = generateRandomMatrix();

        // Dibujar la matriz
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                g2d.setColor(matrix[i][j]);
                g2d.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private Color[][] generateRandomMatrix() {
        Color[][] matrix = new Color[MATRIX_SIZE][MATRIX_SIZE];
        Random random = new Random();

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                // Generar color aleatorio (rojo o negro)
                matrix[i][j] = random.nextBoolean() ? Color.RED : Color.BLACK;
            }
        }

        return matrix;
    }
}
