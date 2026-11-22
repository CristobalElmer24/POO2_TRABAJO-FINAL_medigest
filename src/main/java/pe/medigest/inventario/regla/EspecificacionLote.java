package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

import java.util.ArrayList;
import java.util.List;

/**
 * Condición que un lote cumple o no. Se pueden combinar con y(), o() y no(),
 * por ejemplo: {@code new LoteVigente().y(new LoteConStock())}.
 */
public interface EspecificacionLote {

    boolean cumple(LoteAlmacen lote);

    default EspecificacionLote y(EspecificacionLote otra) {
        return new EspecificacionY(this, otra);
    }

    default EspecificacionLote o(EspecificacionLote otra) {
        return new EspecificacionO(this, otra);
    }

    default EspecificacionLote no() {
        return new EspecificacionNo(this);
    }

    /** Devuelve solo los lotes que cumplen la condición. */
    default List<LoteAlmacen> filtrar(List<LoteAlmacen> lotes) {
        List<LoteAlmacen> resultado = new ArrayList<>();
        for (LoteAlmacen lote : lotes) {
            if (cumple(lote)) {
                resultado.add(lote);
            }
        }
        return resultado;
    }
}
