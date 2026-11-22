package pe.medigest.inventario.evento;

/** Entró mercadería: un lote nuevo o más unidades de un lote existente. */
public class EventoLoteIngresado extends EventoInventario {
    private final int productoId;
    private final String nroLote;
    private final int cantidad;

    public EventoLoteIngresado(int productoId, String nroLote, int cantidad) {
        this.productoId = productoId;
        this.nroLote = nroLote;
        this.cantidad = cantidad;
    }

    public int getProductoId() { return productoId; }

    public String getNroLote() { return nroLote; }

    public int getCantidad() { return cantidad; }

    @Override
    public String descripcion() {
        return "Ingreso de " + cantidad + " unidades al lote " + nroLote + " (producto " + productoId + ")";
    }
}
