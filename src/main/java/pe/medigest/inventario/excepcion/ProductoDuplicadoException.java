package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** Ya existe un producto con ese código. */
public class ProductoDuplicadoException extends ReglaNegocioException {
    public ProductoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
