package pe.medigest.farmacia.excepcion;

import pe.medigest.farmacia.RecetaInvalidaException;

/** La receta pasó su plazo de vigencia. */
public class RecetaVencidaException extends RecetaInvalidaException {
    public RecetaVencidaException(String mensaje) {
        super(mensaje);
    }
}
