package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.modelo.MotivoRechazo;

/** Cada línea de la receta debe pedir al menos una unidad. */
public class ReglaCantidadesPositivas implements ReglaReceta {
    @Override
    public void verificar(Receta receta) {
        for (DetalleReceta d : receta.getDetalles()) {
            d.validarCantidad();
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.CANTIDAD_INVALIDA; }
}
