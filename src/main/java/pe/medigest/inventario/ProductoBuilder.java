package pe.medigest.inventario;

import pe.medigest.inventario.modelo.CodigoProducto;

/** PATRÓN: Builder. Arma un Producto paso a paso y lo valida al final con build(). */
public class ProductoBuilder {

    private int id;
    private String codigo;
    private String nombre;
    private String presentacion = "Caja";
    private int stockMinimo = 10;

    public ProductoBuilder conId(int id) {
        this.id = id;
        return this;
    }

    public ProductoBuilder conCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    public ProductoBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public ProductoBuilder conPresentacion(String presentacion) {
        this.presentacion = presentacion;
        return this;
    }

    public ProductoBuilder conStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
        return this;
    }

    public Producto build() {
        String cod = CodigoProducto.de(codigo).getValor();
        Producto p = new Producto(id, cod, nombre, presentacion, stockMinimo);
        p.validar();
        return p;
    }
}
