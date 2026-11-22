package pe.medigest.farmacia;

/** Qué lote se usó y cuántas unidades se entregaron de un medicamento (una receta puede usar varios lotes). */
public class DetalleTicket {

    private final int productoId;
    private final String nombreProducto;
    private final int loteId;
    private final String nroLote;
    private final int cantidad;

    public DetalleTicket(int productoId, String nombreProducto, int loteId, String nroLote, int cantidad) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.loteId = loteId;
        this.nroLote = nroLote;
        this.cantidad = cantidad;
    }

    public int getProductoId() { return productoId; }

    public String getNombreProducto() { return nombreProducto; }

    public int getLoteId() { return loteId; }

    public String getNroLote() { return nroLote; }

    public int getCantidad() { return cantidad; }
}
