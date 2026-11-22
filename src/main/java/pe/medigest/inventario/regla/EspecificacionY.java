package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

/** Se cumple si se cumplen las dos condiciones. */
public class EspecificacionY implements EspecificacionLote {
    private final EspecificacionLote izquierda;
    private final EspecificacionLote derecha;

    public EspecificacionY(EspecificacionLote izquierda, EspecificacionLote derecha) {
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return izquierda.cumple(lote) && derecha.cumple(lote);
    }
}
