package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

/** Lote con al menos una unidad disponible. */
public class LoteConStock implements EspecificacionLote {
    @Override
    public boolean cumple(LoteAlmacen lote) {
        return lote.getCantidadDisponible() > 0;
    }
}
