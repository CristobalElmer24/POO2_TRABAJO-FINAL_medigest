package pe.medigest.inventario;

import java.util.List;

/** PATRÓN: Strategy. Define cómo se eligen los lotes cuando hay que sacar stock. */
public interface EstrategiaSeleccionLote {

    /**
     * Devuelve los lotes (en orden) que alcanzan para cubrir la cantidad pedida.
     * Nunca incluye lotes vencidos ni lotes sin stock.
     *
     * @throws pe.medigest.inventario.StockInsuficienteException si no alcanza con lotes vigentes
     * @throws pe.medigest.inventario.LoteVencidoException si lo único que hay son lotes vencidos
     */
    List<LoteAlmacen> seleccionar(List<LoteAlmacen> lotes, int cantidadRequerida);

    String getNombre();
}
