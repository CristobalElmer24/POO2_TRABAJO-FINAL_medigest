package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.excepcion.RecetaNoEncontradaException;
import pe.medigest.farmacia.modelo.MotivoRechazo;

/** La receta tiene que existir en el sistema. */
public class ReglaRecetaExiste implements ReglaReceta {
    @Override
    public void verificar(Receta receta) {
        if (receta == null) {
            throw new RecetaNoEncontradaException("La receta no existe en el sistema.");
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.RECETA_INEXISTENTE; }
}
