package pe.medigest.inventario.modelo;

import pe.medigest.comun.ReglaNegocioException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Intervalo de fechas cerrado [desde, hasta], para filtrar vencimientos y movimientos. */
public final class RangoFechas {

    private final LocalDate desde;
    private final LocalDate hasta;

    public RangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ReglaNegocioException("El rango de fechas necesita fecha inicial y final.");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaNegocioException("La fecha final no puede ser anterior a la inicial.");
        }
        this.desde = desde;
        this.hasta = hasta;
    }

    /** Desde hoy hasta dentro de "dias" días. */
    public static RangoFechas proximosDias(int dias) {
        LocalDate hoy = LocalDate.now();
        return new RangoFechas(hoy, hoy.plusDays(dias));
    }

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public long dias() {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    public LocalDate getDesde() { return desde; }

    public LocalDate getHasta() { return hasta; }

    @Override
    public String toString() { return desde + " a " + hasta; }
}
