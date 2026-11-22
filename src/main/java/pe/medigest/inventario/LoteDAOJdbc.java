package pe.medigest.inventario;

import pe.medigest.comun.JdbcUtil;
import pe.medigest.comun.JdbcUtil.FilaMapper;

import java.time.LocalDate;
import java.util.List;

public class LoteDAOJdbc implements LoteDAO {

    private static final FilaMapper<LoteAlmacen> MAPPER = rs -> new LoteAlmacen(
            rs.getInt("id"), rs.getInt("producto_id"), rs.getString("nro_lote"),
            LocalDate.parse(rs.getString("fecha_vencimiento")), rs.getInt("cantidad_disponible"));

    @Override
    public void insertar(LoteAlmacen l) {
        int id = JdbcUtil.insertar(
                "INSERT INTO lote_almacen (producto_id, nro_lote, fecha_vencimiento, cantidad_disponible) VALUES (?,?,?,?)",
                l.getProductoId(), l.getNroLote(), l.getFechaVencimiento().toString(), l.getCantidadDisponible());
        l.setId(id);
    }

    @Override
    public void actualizar(LoteAlmacen l) {
        JdbcUtil.actualizar(
                "UPDATE lote_almacen SET nro_lote = ?, fecha_vencimiento = ?, cantidad_disponible = ? WHERE id = ?",
                l.getNroLote(), l.getFechaVencimiento().toString(), l.getCantidadDisponible(), l.getId());
    }

    @Override
    public void eliminar(int id) {
        JdbcUtil.actualizar("DELETE FROM lote_almacen WHERE id = ?", id);
    }

    @Override
    public LoteAlmacen buscarPorId(int id) {
        return JdbcUtil.consultarUno("SELECT * FROM lote_almacen WHERE id = ?", MAPPER, id);
    }

    @Override
    public List<LoteAlmacen> listar() {
        return JdbcUtil.consultar("SELECT * FROM lote_almacen ORDER BY producto_id, fecha_vencimiento, id", MAPPER);
    }

    @Override
    public List<LoteAlmacen> listarPorProducto(int productoId) {
        return JdbcUtil.consultar("SELECT * FROM lote_almacen WHERE producto_id = ? ORDER BY id", MAPPER, productoId);
    }

    @Override
    public LoteAlmacen buscarPorProductoYNroLote(int productoId, String nroLote) {
        return JdbcUtil.consultarUno("SELECT * FROM lote_almacen WHERE producto_id = ? AND nro_lote = ?",
                MAPPER, productoId, nroLote);
    }

    @Override
    public void actualizarCantidad(int loteId, int nuevaCantidad) {
        JdbcUtil.actualizar("UPDATE lote_almacen SET cantidad_disponible = ? WHERE id = ?", nuevaCantidad, loteId);
    }
}
