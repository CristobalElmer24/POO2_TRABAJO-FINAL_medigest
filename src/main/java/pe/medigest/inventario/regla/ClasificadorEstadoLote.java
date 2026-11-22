package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.modelo.EstadoLote;

import java.time.LocalDate;

/** Decide en qué {@link EstadoLote} está un lote en una fecha dada. */
public class ClasificadorEstadoLote {

    private final int diasPorVencer;

    public ClasificadorEstadoLote(int diasPorVencer) {
        this.diasPorVencer = diasPorVencer;
    }

    public ClasificadorEstadoLote(ConfiguracionInventario config) {
        this(config.getDiasAlertaVencimiento());
    }

    public EstadoLote clasificar(LoteAlmacen lote, LocalDate hoy) {
        if (lote.estaVencido(hoy)) {
            return EstadoLote.VENCIDO;
        }
        if (lote.getCantidadDisponible() <= 0) {
            return EstadoLote.AGOTADO;
        }
        if (!lote.getFechaVencimiento().isAfter(hoy.plusDays(diasPorVencer))) {
            return EstadoLote.POR_VENCER;
        }
        return EstadoLote.VIGENTE;
    }

    public EstadoLote clasificar(LoteAlmacen lote) {
        return clasificar(lote, LocalDate.now());
    }
}
