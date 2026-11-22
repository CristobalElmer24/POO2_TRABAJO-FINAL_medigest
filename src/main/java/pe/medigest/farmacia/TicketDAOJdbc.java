package pe.medigest.farmacia;

import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.JdbcUtil;
import pe.medigest.comun.JdbcUtil.FilaMapper;

import java.time.LocalDateTime;
import java.util.List;

public class TicketDAOJdbc implements TicketDAO {

    private static final String SELECT_BASE =
            "SELECT t.id, t.receta_id, r.codigo AS receta_codigo, r.paciente_nombre, t.fecha_hora, "
                    + "t.usuario_id, u.nombre_usuario, t.estado "
                    + "FROM ticket_farmacia t "
                    + "JOIN receta r ON r.id = t.receta_id "
                    + "JOIN usuario u ON u.id = t.usuario_id ";

    private static final FilaMapper<TicketFarmacia> MAPPER = rs -> new TicketFarmacia(
            rs.getInt("id"), rs.getInt("receta_id"), rs.getString("receta_codigo"), rs.getString("paciente_nombre"),
            LocalDateTime.parse(rs.getString("fecha_hora")), rs.getInt("usuario_id"),
            rs.getString("nombre_usuario"), rs.getString("estado"));

    @Override
    public void insertar(TicketFarmacia t) {
        ConexionBD.getInstancia().enTransaccionVoid(() -> {
            int id = JdbcUtil.insertar(
                    "INSERT INTO ticket_farmacia (receta_id, fecha_hora, usuario_id, estado) VALUES (?,?,?,?)",
                    t.getRecetaId(), t.getFechaHora().toString(), t.getUsuarioId(), t.getEstado());
            t.setId(id);
            for (DetalleTicket d : t.getDetalles()) {
                JdbcUtil.insertar(
                        "INSERT INTO detalle_ticket (ticket_id, producto_id, lote_id, cantidad) VALUES (?,?,?,?)",
                        id, d.getProductoId(), d.getLoteId(), d.getCantidad());
            }
        });
    }

    @Override
    public TicketFarmacia buscarPorId(int id) {
        TicketFarmacia ticket = JdbcUtil.consultarUno(SELECT_BASE + "WHERE t.id = ?", MAPPER, id);
        if (ticket != null) {
            cargarDetalles(ticket);
        }
        return ticket;
    }

    @Override
    public void actualizarEstado(int ticketId, String estado) {
        JdbcUtil.actualizar("UPDATE ticket_farmacia SET estado = ? WHERE id = ?", estado, ticketId);
    }

    @Override
    public List<TicketFarmacia> listarPorReceta(String codigoReceta) {
        return conDetalles(JdbcUtil.consultar(SELECT_BASE + "WHERE r.codigo = ? ORDER BY t.id DESC", MAPPER, codigoReceta));
    }

    @Override
    public List<TicketFarmacia> listarPorPaciente(String texto) {
        String patron = "%" + texto + "%";
        return conDetalles(JdbcUtil.consultar(
                SELECT_BASE + "WHERE r.paciente_nombre LIKE ? OR r.paciente_documento LIKE ? ORDER BY t.id DESC",
                MAPPER, patron, patron));
    }

    @Override
    public List<TicketFarmacia> listarUltimos(int cantidad) {
        return conDetalles(JdbcUtil.consultar(SELECT_BASE + "ORDER BY t.id DESC LIMIT ?", MAPPER, cantidad));
    }

    private List<TicketFarmacia> conDetalles(List<TicketFarmacia> tickets) {
        for (TicketFarmacia t : tickets) {
            cargarDetalles(t);
        }
        return tickets;
    }

    private void cargarDetalles(TicketFarmacia ticket) {
        List<DetalleTicket> detalles = JdbcUtil.consultar(
                "SELECT d.producto_id, p.nombre, d.lote_id, l.nro_lote, d.cantidad "
                        + "FROM detalle_ticket d "
                        + "JOIN producto p ON p.id = d.producto_id "
                        + "JOIN lote_almacen l ON l.id = d.lote_id "
                        + "WHERE d.ticket_id = ? ORDER BY d.id",
                rs -> new DetalleTicket(rs.getInt("producto_id"), rs.getString("nombre"),
                        rs.getInt("lote_id"), rs.getString("nro_lote"), rs.getInt("cantidad")),
                ticket.getId());
        for (DetalleTicket d : detalles) {
            ticket.agregarDetalle(d);
        }
    }
}
