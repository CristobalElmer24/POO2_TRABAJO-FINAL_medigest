package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** Se pidió un lote que no existe. */
public class LoteNoEncontradoException extends ReglaNegocioException {
    public LoteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
