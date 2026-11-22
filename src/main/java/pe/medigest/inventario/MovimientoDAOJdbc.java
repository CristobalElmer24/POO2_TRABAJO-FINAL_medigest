package pe.medigest.inventario;

import pe.medigest.comun.JdbcUtil;

import java.time.LocalDateTime;
import java.util.List;

public class MovimientoDAOJdbc implements MovimientoDAO {

    @Override
    public void insertar(MovimientoInventario m) {
        int id = JdbcUtil.insertar(
                "INSERT INTO movimiento_inventario (lote_id, cantidad, tipo, motivo, fecha) VALUES (?,?,?,?,?)",
                m.getLoteId(), m.getCantidad(), m.getTipo(), m.getMotivo(), m.getFecha().toString());
        m.setId(id);
    }

    @Override
    public List<MovimientoInventario> listarUltimos(int cantidad) {
        String sql = "SELECT m.id, m.lote_id, m.cantidad, m.tipo, m.motivo, m.fecha, "
                + "p.nombre || ' / ' || l.nro_lote AS detalle "
                + "FROM movimiento_inventario m "
                + "JOIN lote_almacen l ON l.id = m.lote_id "
                + "JOIN producto p ON p.id = l.producto_id "
                + "ORDER BY m.id DESC LIMIT ?";
        return JdbcUtil.consultar(sql, rs -> {
            MovimientoInventario m = MovimientoFactory.crear(
                    rs.getString("tipo"), rs.getInt("lote_id"), rs.getInt("cantidad"), rs.getString("motivo"));
            m.setId(rs.getInt("id"));
            m.setFecha(LocalDateTime.parse(rs.getString("fecha")));
            m.setDetalleLote(rs.getString("detalle"));
            return m;
        }, cantidad);
    }
}
