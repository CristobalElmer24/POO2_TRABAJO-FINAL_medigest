package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

public class LoteVencidoException extends ReglaNegocioException {
    public LoteVencidoException(String mensaje) {
        super(mensaje);
    }
}
