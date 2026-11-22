package pe.medigest.farmacia.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** El ticket pedido no existe. */
public class TicketNoEncontradoException extends ReglaNegocioException {
    public TicketNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
