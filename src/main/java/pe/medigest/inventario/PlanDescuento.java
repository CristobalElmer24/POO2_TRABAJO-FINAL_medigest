package pe.medigest.inventario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simula de qué lotes saldría una cantidad pedida, sin modificar nada.
 * Sirve para mostrar "se usarán estos lotes" antes de confirmar una dispensación.
 */
public class PlanDescuento {

    private final List<ResultadoDescuento> pasos;

    private PlanDescuento(List<ResultadoDescuento> pasos) {
        this.pasos = pasos;
    }

    public static PlanDescuento simular(EstrategiaSeleccionLote estrategia, List<LoteAlmacen> lotes, int cantidad) {
        List<LoteAlmacen> elegidos = estrategia.seleccionar(lotes, cantidad);
        List<ResultadoDescuento> pasos = new ArrayList<>();
        int restante = cantidad;
        for (LoteAlmacen lote : elegidos) {
            int usar = Math.min(lote.getCantidadDisponible(), restante);
            pasos.add(new ResultadoDescuento(lote, usar));
            restante -= usar;
        }
        return new PlanDescuento(pasos);
    }

    public List<ResultadoDescuento> getPasos() { return Collections.unmodifiableList(pasos); }

    public int totalUnidades() {
        int total = 0;
        for (ResultadoDescuento p : pasos) {
            total += p.getCantidad();
        }
        return total;
    }

    public int cantidadLotes() { return pasos.size(); }
}
