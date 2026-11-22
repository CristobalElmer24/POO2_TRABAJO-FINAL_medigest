package pe.medigest.comun;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;

/**
 * PATRÓN: Singleton.
 * Una sola conexión a SQLite compartida por todos los módulos.
 *
 * Como hay un hilo de fondo (MonitorInventario) y el hilo de la interfaz usando la misma conexión,
 * las operaciones que necesitan consistencia se hacen dentro de synchronized (CANDADO)
 * o con enTransaccion(), que ya incluye el candado.
 *
 * IMPORTANTE: nadie debe cerrar la conexión compartida (no usar try-with-resources sobre ella).
 */
public class ConexionBD {

    /** Candado común: solo un hilo a la vez puede estar en una transacción o leyendo el inventario. */
    public static final Object CANDADO = new Object();

    private static ConexionBD instancia;
    private static String url = "jdbc:sqlite:medigest.db";

    private Connection conexion;

    private ConexionBD() { }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /** Solo para pruebas (por ejemplo "jdbc:sqlite::memory:"). Debe llamarse antes de abrir la primera conexión. */
    public static synchronized void configurarUrl(String nuevaUrl) {
        if (instancia != null && instancia.conexion != null) {
            throw new IllegalStateException("La conexión ya fue abierta.");
        }
        url = nuevaUrl;
    }

    public synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(url);
                try (Statement st = conexion.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                    st.execute("PRAGMA busy_timeout = 3000");
                }
            }
            return conexion;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo conectar a la base de datos.", e);
        }
    }

    /**
     * Ejecuta la acción dentro de una transacción: si todo sale bien hace commit y si algo falla hace rollback.
     * Si ya hay una transacción abierta (por ejemplo, dispensar llama a descontarStock) se une a ella
     * en lugar de abrir otra.
     */
    public <T> T enTransaccion(Supplier<T> accion) {
        synchronized (CANDADO) {
            Connection c = getConexion();
            try {
                if (!c.getAutoCommit()) {
                    return accion.get();
                }
                c.setAutoCommit(false);
            } catch (SQLException e) {
                throw new AccesoDatosException("No se pudo iniciar la transacción.", e);
            }
            try {
                T resultado = accion.get();
                c.commit();
                return resultado;
            } catch (Exception e) {
                try {
                    c.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new AccesoDatosException("Error dentro de la transacción.", e);
            } finally {
                try {
                    c.setAutoCommit(true);
                } catch (SQLException ignorada) {
                    // si falla aquí ya no hay nada más que hacer
                }
            }
        }
    }

    /** Igual que enTransaccion pero para acciones que no devuelven nada. */
    public void enTransaccionVoid(Runnable accion) {
        enTransaccion(() -> {
            accion.run();
            return null;
        });
    }
}
