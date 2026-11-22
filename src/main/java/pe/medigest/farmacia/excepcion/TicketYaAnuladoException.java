package pe.medigest.farmacia.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** El ticket ya estaba anulado. */
public class TicketYaAnuladoException extends ReglaNegocioException {
    public TicketYaAnuladoException(String mensaje) {
        super(mensaje);
    }
}
