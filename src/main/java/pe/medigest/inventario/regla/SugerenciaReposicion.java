package pe.medigest.inventario.regla;

/** Cuántas unidades conviene pedir de un producto, y por qué. */
public class SugerenciaReposicion {
    private final int productoId;
    private final String producto;
    private final int unidadesSugeridas;
    private final String motivo;

    public SugerenciaReposicion(int productoId, String producto, int unidadesSugeridas, String motivo) {
        this.productoId = productoId;
        this.producto = producto;
        this.unidadesSugeridas = unidadesSugeridas;
        this.motivo = motivo;
    }

    public int getProductoId() { return productoId; }

    public String getProducto() { return producto; }

    public int getUnidadesSugeridas() { return unidadesSugeridas; }

    public String getMotivo() { return motivo; }

    public boolean hayQueReponer() { return unidadesSugeridas > 0; }

    @Override
    public String toString() {
        return producto + ": pedir " + unidadesSugeridas + " (" + motivo + ")";
    }
}
