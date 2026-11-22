package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

/** Se cumple cuando la condición original no se cumple. */
public class EspecificacionNo implements EspecificacionLote {
    private final EspecificacionLote original;

    public EspecificacionNo(EspecificacionLote original) {
        this.original = original;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return !original.cumple(lote);
    }
}
