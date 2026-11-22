package pe.medigest.farmacia;

import pe.medigest.comun.ReglaNegocioException;

public class RecetaInvalidaException extends ReglaNegocioException {
    public RecetaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
