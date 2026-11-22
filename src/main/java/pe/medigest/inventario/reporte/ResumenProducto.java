package pe.medigest.inventario.reporte;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.modelo.NivelStock;
import pe.medigest.inventario.regla.ClasificadorNivelStock;

/** Foto de un producto: cuánto tiene, cuánto se puede usar, cuánto está vencido y qué nivel de stock es. */
public class ResumenProducto {

    private final Producto producto;
    private final int stockTotal;
    private final int stockUtilizable;
    private final int unidadesVencidas;
    private final NivelStock nivel;

    public ResumenProducto(Producto producto, ClasificadorNivelStock clasificador) {
        this.producto = producto;
        this.stockTotal = producto.stockTotal();
        this.stockUtilizable = producto.stockUtilizable();
        int vencidas = 0;
        for (LoteAlmacen l : producto.getLotes()) {
            if (l.estaVencido()) {
                vencidas += l.getCantidadDisponible();
            }
        }
        this.unidadesVencidas = vencidas;
        this.nivel = clasificador.clasificar(stockUtilizable, producto.getStockMinimo());
    }

    public Producto getProducto() { return producto; }

    public int getStockTotal() { return stockTotal; }

    public int getStockUtilizable() { return stockUtilizable; }

    public int getUnidadesVencidas() { return unidadesVencidas; }

    public NivelStock getNivel() { return nivel; }
}
