package Controlador;

import Modelo.Tabla;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaTablas {

    /**
     * Retorna todas las tablas activas ordenadas por numTabla.
     */
    public static List<Tabla> getTablasActivas(Connection conn) throws SQLException {
        List<Tabla> lista = new ArrayList<>();
        String sql = "SELECT numTabla, n1,n2,n3,n4,n5, n6,n7,n8,n9,n10, " +
                     "n11,n12,n13,n14,n15, n16,n17,n18,n19,n20, n21,n22,n23,n24,n25 " +
                     "FROM Tablas WHERE activo = 1 ORDER BY numTabla";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Tabla(
                    rs.getInt("numTabla"), true,
                    rs.getInt("n1"),  rs.getInt("n2"),  rs.getInt("n3"),  rs.getInt("n4"),  rs.getInt("n5"),
                    rs.getInt("n6"),  rs.getInt("n7"),  rs.getInt("n8"),  rs.getInt("n9"),  rs.getInt("n10"),
                    rs.getInt("n11"), rs.getInt("n12"), rs.getInt("n13"), rs.getInt("n14"), rs.getInt("n15"),
                    rs.getInt("n16"), rs.getInt("n17"), rs.getInt("n18"), rs.getInt("n19"), rs.getInt("n20"),
                    rs.getInt("n21"), rs.getInt("n22"), rs.getInt("n23"), rs.getInt("n24"), rs.getInt("n25")
                ));
            }
        }
        return lista;
    }

    /**
     * Construye el contenido del QR: numTabla-n1-n2-...-n25
     */
    public static String buildQRContent(Tabla t) {
        StringBuilder sb = new StringBuilder().append(t.getNumTabla());
        int[] nums = {
            t.getN1(),  t.getN2(),  t.getN3(),  t.getN4(),  t.getN5(),
            t.getN6(),  t.getN7(),  t.getN8(),  t.getN9(),  t.getN10(),
            t.getN11(), t.getN12(), t.getN13(), t.getN14(), t.getN15(),
            t.getN16(), t.getN17(), t.getN18(), t.getN19(), t.getN20(),
            t.getN21(), t.getN22(), t.getN23(), t.getN24(), t.getN25()
        };
        for (int n : nums) sb.append('-').append(n);
        return sb.toString();
    }
}
