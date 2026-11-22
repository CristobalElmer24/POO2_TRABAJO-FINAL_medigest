package pe.medigest.farmacia.excepcion;

import pe.medigest.farmacia.RecetaInvalidaException;

/** La receta ya se entregó antes. */
public class RecetaYaDispensadaException extends RecetaInvalidaException {
    public RecetaYaDispensadaException(String mensaje) {
        super(mensaje);
    }
}
