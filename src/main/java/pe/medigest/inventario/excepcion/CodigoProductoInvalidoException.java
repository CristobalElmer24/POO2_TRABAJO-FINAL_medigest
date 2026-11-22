package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** El código del producto no tiene un formato aceptable. */
public class CodigoProductoInvalidoException extends ReglaNegocioException {
    public CodigoProductoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
