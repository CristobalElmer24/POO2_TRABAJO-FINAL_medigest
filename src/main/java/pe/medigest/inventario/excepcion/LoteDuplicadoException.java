package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** Ya existe un lote con ese número para el mismo producto. */
public class LoteDuplicadoException extends ReglaNegocioException {
    public LoteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
