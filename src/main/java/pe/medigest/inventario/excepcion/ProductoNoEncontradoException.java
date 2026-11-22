package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** Se pidió un producto que no está en el catálogo. */
public class ProductoNoEncontradoException extends ReglaNegocioException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
