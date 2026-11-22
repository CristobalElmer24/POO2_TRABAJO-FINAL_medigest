package pe.medigest.comun;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Bitácora de auditoría: quién hizo qué, sobre qué, cuándo y con qué resultado. */
public class AuditoriaLogger {

    /** Registra el evento. Si la bitácora falla, no se tumba la operación principal: solo se avisa por consola. */
    public static void registrar(String usuario, String accion, String entidad, String resultado) {
        String sql = "INSERT INTO bitacora_auditoria (usuario, accion, entidad, fecha_hora, resultado) VALUES (?,?,?,?,?)";
        synchronized (ConexionBD.CANDADO) {
            try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
                ps.setString(1, usuario);
                ps.setString(2, accion);
                ps.setString(3, entidad);
                ps.setString(4, LocalDateTime.now().toString());
                ps.setString(5, resultado);
                ps.executeUpdate();
            } catch (SQLException | RuntimeException e) {
                System.err.println("No se pudo registrar la auditoría: " + e.getMessage());
            }
        }
    }

    /** Últimos eventos, el más reciente primero. Cada fila: usuario, acción, entidad, fecha y hora, resultado. */
    public static List<String[]> listarUltimos(int cantidad) {
        List<String[]> filas = new ArrayList<>();
        String sql = "SELECT usuario, accion, entidad, fecha_hora, resultado FROM bitacora_auditoria ORDER BY id DESC LIMIT ?";
        synchronized (ConexionBD.CANDADO) {
            try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
                ps.setInt(1, cantidad);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5)});
                    }
                }
            } catch (SQLException e) {
                throw new AccesoDatosException("No se pudo leer la bitácora.", e);
            }
        }
        return filas;
    }
}
