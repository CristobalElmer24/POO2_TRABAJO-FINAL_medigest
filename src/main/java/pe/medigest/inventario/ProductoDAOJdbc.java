package pe.medigest.inventario;

import pe.medigest.comun.JdbcUtil;
import pe.medigest.comun.JdbcUtil.FilaMapper;

import java.util.List;

public class ProductoDAOJdbc implements ProductoDAO {

    private static final FilaMapper<Producto> MAPPER = rs -> new Producto(
            rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"),
            rs.getString("presentacion"), rs.getInt("stock_minimo"));

    @Override
    public void insertar(Producto p) {
        int id = JdbcUtil.insertar(
                "INSERT INTO producto (codigo, nombre, presentacion, stock_minimo) VALUES (?,?,?,?)",
                p.getCodigo(), p.getNombre(), p.getPresentacion(), p.getStockMinimo());
        p.setId(id);
    }

    @Override
    public void actualizar(Producto p) {
        JdbcUtil.actualizar(
                "UPDATE producto SET codigo = ?, nombre = ?, presentacion = ?, stock_minimo = ? WHERE id = ?",
                p.getCodigo(), p.getNombre(), p.getPresentacion(), p.getStockMinimo(), p.getId());
    }

    @Override
    public void eliminar(int id) {
        JdbcUtil.actualizar("DELETE FROM producto WHERE id = ?", id);
    }

    @Override
    public Producto buscarPorId(int id) {
        return JdbcUtil.consultarUno("SELECT * FROM producto WHERE id = ?", MAPPER, id);
    }

    @Override
    public Producto buscarPorCodigo(String codigo) {
        return JdbcUtil.consultarUno("SELECT * FROM producto WHERE codigo = ?", MAPPER, codigo);
    }

    @Override
    public List<Producto> listar() {
        return JdbcUtil.consultar("SELECT * FROM producto ORDER BY nombre", MAPPER);
    }
}
