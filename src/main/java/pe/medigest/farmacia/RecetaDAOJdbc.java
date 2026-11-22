package pe.medigest.farmacia;

import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.JdbcUtil;

import java.time.LocalDate;
import java.util.List;

public class RecetaDAOJdbc implements RecetaDAO {

    @Override
    public Receta buscarPorCodigo(String codigo) {
        Receta receta = JdbcUtil.consultarUno(
                "SELECT id, codigo, estado, fecha_emision, paciente_nombre, paciente_documento FROM receta WHERE codigo = ?",
                rs -> new Receta(rs.getInt("id"), rs.getString("codigo"), rs.getString("estado"),
                        LocalDate.parse(rs.getString("fecha_emision")),
                        rs.getString("paciente_nombre"), rs.getString("paciente_documento")),
                codigo);
        if (receta == null) {
            return null;
        }
        List<DetalleReceta> detalles = JdbcUtil.consultar(
                "SELECT d.producto_id, p.nombre, d.cantidad FROM detalle_receta d "
                        + "JOIN producto p ON p.id = d.producto_id WHERE d.receta_id = ? ORDER BY d.id",
                rs -> new DetalleReceta(rs.getInt("producto_id"), rs.getString("nombre"), rs.getInt("cantidad")),
                receta.getId());
        for (DetalleReceta d : detalles) {
            receta.agregarDetalle(d);
        }
        return receta;
    }

    @Override
    public void insertar(Receta r) {
        ConexionBD.getInstancia().enTransaccionVoid(() -> {
            int id = JdbcUtil.insertar(
                    "INSERT INTO receta (codigo, estado, fecha_emision, paciente_nombre, paciente_documento) VALUES (?,?,?,?,?)",
                    r.getCodigo(), r.getEstado(), r.getFechaEmision().toString(),
                    r.getPacienteNombre(), r.getPacienteDocumento());
            r.setId(id);
            for (DetalleReceta d : r.getDetalles()) {
                JdbcUtil.insertar("INSERT INTO detalle_receta (receta_id, producto_id, cantidad) VALUES (?,?,?)",
                        id, d.getProductoId(), d.getCantidad());
            }
        });
    }

    @Override
    public void actualizarEstado(int recetaId, String estado) {
        JdbcUtil.actualizar("UPDATE receta SET estado = ? WHERE id = ?", estado, recetaId);
    }
}
