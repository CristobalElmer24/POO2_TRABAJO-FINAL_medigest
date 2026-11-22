package pe.medigest.inventario;

/** Cuántas unidades se sacaron (o se sacarían) de un lote concreto. */
public class ResultadoDescuento {
    private final LoteAlmacen lote;
    private final int cantidad;

    public ResultadoDescuento(LoteAlmacen lote, int cantidad) {
        this.lote = lote;
        this.cantidad = cantidad;
    }

    public LoteAlmacen getLote() { return lote; }

    public int getCantidad() { return cantidad; }

    @Override
    public String toString() {
        return lote.getNroLote() + " x" + cantidad;
    }
}
