package pe.medigest.inventario;

import pe.medigest.comun.GenericDAO;

public interface ProductoDAO extends GenericDAO<Producto> {
    Producto buscarPorCodigo(String codigo);
}
