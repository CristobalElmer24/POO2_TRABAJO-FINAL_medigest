package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.modelo.MotivoRechazo;

import java.util.HashSet;
import java.util.Set;

/** Un mismo medicamento no debe aparecer en dos líneas de la receta (se podría dispensar de más). */
public class ReglaSinRepetidos implements ReglaReceta {
    @Override
    public void verificar(Receta receta) {
        Set<Integer> vistos = new HashSet<>();
        for (DetalleReceta d : receta.getDetalles()) {
            if (!vistos.add(d.getProductoId())) {
                throw new RecetaInvalidaException("La receta " + receta.getCodigo() + " repite el medicamento "
                        + d.getNombreProducto() + ".");
            }
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.MEDICAMENTO_REPETIDO; }
}
