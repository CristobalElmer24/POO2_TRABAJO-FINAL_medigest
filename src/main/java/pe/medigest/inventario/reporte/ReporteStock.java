package pe.medigest.inventario.reporte;

import pe.medigest.inventario.Producto;
import pe.medigest.inventario.regla.ClasificadorNivelStock;

import java.util.Arrays;
import java.util.List;

/** Reporte de existencias por producto, con nivel de stock (los más urgentes primero). */
public class ReporteStock {

    private final ClasificadorNivelStock clasificador = new ClasificadorNivelStock();

    public TablaReporte generar(List<Producto> productos) {
        TablaReporte tabla = new TablaReporte("REPORTE DE STOCK",
                Arrays.asList("Código", "Producto", "Total", "Utilizable", "Vencidas", "Mínimo", "Nivel"));
        productos.stream()
                .map(p -> new ResumenProducto(p, clasificador))
                .sorted((a, b) -> Integer.compare(a.getNivel().getPrioridad(), b.getNivel().getPrioridad()))
                .forEach(r -> tabla.agregarFila(r.getProducto().getCodigo(), r.getProducto().getNombre(),
                        String.valueOf(r.getStockTotal()), String.valueOf(r.getStockUtilizable()),
                        String.valueOf(r.getUnidadesVencidas()), String.valueOf(r.getProducto().getStockMinimo()),
                        r.getNivel().name()));
        return tabla;
    }
}
