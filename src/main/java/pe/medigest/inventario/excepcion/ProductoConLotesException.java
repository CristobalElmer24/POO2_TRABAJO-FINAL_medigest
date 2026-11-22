package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** No se puede eliminar un producto que ya tiene lotes registrados. */
public class ProductoConLotesException extends ReglaNegocioException {
    public ProductoConLotesException(String mensaje) {
        super(mensaje);
    }
}
