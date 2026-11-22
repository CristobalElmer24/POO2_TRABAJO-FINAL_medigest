package pe.medigest.inventario;

import pe.medigest.comun.GenericDAO;

import java.util.List;

public interface LoteDAO extends GenericDAO<LoteAlmacen> {
    List<LoteAlmacen> listarPorProducto(int productoId);

    LoteAlmacen buscarPorProductoYNroLote(int productoId, String nroLote);

    void actualizarCantidad(int loteId, int nuevaCantidad);
}
