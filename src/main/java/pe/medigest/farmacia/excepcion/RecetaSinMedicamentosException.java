package pe.medigest.farmacia.excepcion;

import pe.medigest.farmacia.RecetaInvalidaException;

/** La receta no tiene medicamentos o tiene cantidades inválidas. */
public class RecetaSinMedicamentosException extends RecetaInvalidaException {
    public RecetaSinMedicamentosException(String mensaje) {
        super(mensaje);
    }
}
