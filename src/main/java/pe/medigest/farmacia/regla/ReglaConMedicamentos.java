package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.excepcion.RecetaSinMedicamentosException;
import pe.medigest.farmacia.modelo.MotivoRechazo;

/** La receta debe indicar al menos un medicamento. */
public class ReglaConMedicamentos implements ReglaReceta {
    @Override
    public void verificar(Receta receta) {
        if (receta.getDetalles().isEmpty()) {
            throw new RecetaSinMedicamentosException("La receta " + receta.getCodigo() + " no tiene medicamentos.");
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.SIN_MEDICAMENTOS; }
}
