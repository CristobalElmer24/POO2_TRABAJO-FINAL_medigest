package pe.medigest.inventario.excepcion;

import pe.medigest.comun.ReglaNegocioException;

/** El ajuste de stock no es válido (diferencia cero, sin motivo o deja el lote en negativo). */
public class AjusteInvalidoException extends ReglaNegocioException {
    public AjusteInvalidoException(String mensaje) {
        super(mensaje);
    }
}
