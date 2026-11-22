package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

/** Se cumple si se cumple al menos una de las dos condiciones. */
public class EspecificacionO implements EspecificacionLote {
    private final EspecificacionLote izquierda;
    private final EspecificacionLote derecha;

    public EspecificacionO(EspecificacionLote izquierda, EspecificacionLote derecha) {
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return izquierda.cumple(lote) || derecha.cumple(lote);
    }
}
