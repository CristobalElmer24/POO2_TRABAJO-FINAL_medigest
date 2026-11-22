package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.excepcion.RecetaYaDispensadaException;
import pe.medigest.farmacia.modelo.MotivoRechazo;

/** Una receta solo se entrega una vez. */
public class ReglaNoDispensada implements ReglaReceta {
    @Override
    public void verificar(Receta receta) {
        if (receta.estaDispensada()) {
            throw new RecetaYaDispensadaException("La receta " + receta.getCodigo() + " ya fue dispensada.");
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.YA_DISPENSADA; }
}
