package pe.medigest.farmacia;

/** Una línea de la receta: qué medicamento y cuántas unidades. */
public class DetalleReceta {

    private final int productoId;
    private final String nombreProducto;
    private final int cantidad;

    public DetalleReceta(int productoId, String nombreProducto, int cantidad) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
    }

    public void validarCantidad() {
        if (cantidad <= 0) {
            throw new RecetaInvalidaException("La cantidad de " + nombreProducto + " en la receta debe ser mayor a 0.");
        }
    }

    public int getProductoId() { return productoId; }

    public String getNombreProducto() { return nombreProducto; }

    public int getCantidad() { return cantidad; }
}
