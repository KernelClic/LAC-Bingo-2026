package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexion UNICA a tablas.db para todo el proceso.
 *
 * <p>SQLite admite un solo escritor: dos conexiones del mismo programa se
 * bloquean entre si. La ventana unificada tenia hasta cuatro abiertas (la de
 * autenticacion de Entrada mas una por cada variante), asi que el cursor de
 * lectura de una impedia el DELETE de otra y saltaba
 * {@code [SQLITE_BUSY] The database file is locked}. Centralizando aqui, hay
 * un solo handle y el conflicto no puede darse.</p>
 */
public final class ConexionBD {

    /** Espera antes de rendirse si la base esta ocupada (ms). */
    private static final int ESPERA_OCUPADA = 5000;

    private static Connection unica;

    private ConexionBD() {
    }

    public static synchronized Connection get(String url) throws SQLException {
        if (unica == null || unica.isClosed()) {
            unica = DriverManager.getConnection("jdbc:sqlite:" + url);
            try (Statement p = unica.createStatement()) {
                p.execute("PRAGMA busy_timeout = " + ESPERA_OCUPADA);
            }
        }
        return unica;
    }

    /** Cierra la conexion del proceso; solo tiene sentido al terminar. */
    public static synchronized void cerrar() {
        try {
            if (unica != null && !unica.isClosed()) {
                unica.close();
            }
        } catch (SQLException ex) {
            // cerrando: nada util que hacer
        }
        unica = null;
    }
}
