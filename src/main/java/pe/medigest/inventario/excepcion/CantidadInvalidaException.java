package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** Una cantidad que no cumple la regla (negativa o cero donde debe ser positiva). */
public class CantidadInvalidaException extends ReglaNegocioException {
    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
