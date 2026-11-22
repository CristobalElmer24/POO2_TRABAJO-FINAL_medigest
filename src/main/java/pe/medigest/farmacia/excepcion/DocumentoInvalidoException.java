package pe.medigest.farmacia.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** El documento de identidad no tiene un formato válido. */
public class DocumentoInvalidoException extends ReglaNegocioException {
    public DocumentoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
