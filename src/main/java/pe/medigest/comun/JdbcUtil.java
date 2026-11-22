package pe.medigest.comun;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Métodos de ayuda para que los DAO no repitan siempre el mismo código JDBC. */
public final class JdbcUtil {

    private JdbcUtil() { }

    /** Convierte una fila del ResultSet en un objeto. */
    @FunctionalInterface
    public interface FilaMapper<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    /** INSERT: devuelve el id autogenerado. */
    public static int insertar(String sql, Object... params) {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar en la base de datos: " + e.getMessage(), e);
        }
    }

    /** UPDATE / DELETE: devuelve cuántas filas se afectaron. */
    public static int actualizar(String sql, Object... params) {
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            asignar(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar la base de datos: " + e.getMessage(), e);
        }
    }

    /** SELECT que devuelve una lista. */
    public static <T> List<T> consultar(String sql, FilaMapper<T> mapper, Object... params) {
        List<T> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            asignar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapper.mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar la base de datos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** SELECT que devuelve un solo objeto (o null si no hay filas). */
    public static <T> T consultarUno(String sql, FilaMapper<T> mapper, Object... params) {
        List<T> lista = consultar(sql, mapper, params);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private static void asignar(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
