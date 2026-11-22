package pe.medigest.inventario.regla;

import pe.medigest.inventario.Producto;

/** Si el stock utilizable está en el mínimo o por debajo, pide lo necesario para llegar al doble del mínimo. */
public class ReposicionPorMinimo implements PoliticaReposicion {

    @Override
    public SugerenciaReposicion sugerir(Producto producto) {
        int utilizable = producto.stockUtilizable();
        int minimo = producto.getStockMinimo();
        if (utilizable > minimo) {
            return new SugerenciaReposicion(producto.getId(), producto.getNombre(), 0, "stock sobre el mínimo");
        }
        int objetivo = Math.max(minimo * 2, 1);
        return new SugerenciaReposicion(producto.getId(), producto.getNombre(), objetivo - utilizable,
                "stock " + utilizable + " con mínimo " + minimo);
    }
}
