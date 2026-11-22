package pe.medigest.farmacia.tiempo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Un reloj que siempre marca la misma fecha (o la que se le indique). Pensado para pruebas. */
public class RelojFijo implements Reloj {
    private LocalDateTime momento;

    public RelojFijo(LocalDateTime momento) {
        this.momento = momento;
    }

    public RelojFijo(LocalDate fecha) {
        this(fecha.atStartOfDay());
    }

    public void avanzarDias(long dias) {
        momento = momento.plusDays(dias);
    }

    @Override
    public LocalDate hoy() { return momento.toLocalDate(); }

    @Override
    public LocalDateTime ahora() { return momento; }
}
