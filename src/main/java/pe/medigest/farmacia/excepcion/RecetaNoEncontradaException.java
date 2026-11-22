package pe.medigest.farmacia.excepcion;

import pe.medigest.farmacia.RecetaInvalidaException;

/** La receta pedida no existe en el sistema. */
public class RecetaNoEncontradaException extends RecetaInvalidaException {
    public RecetaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
