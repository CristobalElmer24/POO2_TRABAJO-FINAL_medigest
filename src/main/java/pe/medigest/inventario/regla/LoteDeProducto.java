package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

/** Lote que pertenece a un producto concreto. */
public class LoteDeProducto implements EspecificacionLote {
    private final int productoId;

    public LoteDeProducto(int productoId) {
        this.productoId = productoId;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return lote.getProductoId() == productoId;
    }
}
