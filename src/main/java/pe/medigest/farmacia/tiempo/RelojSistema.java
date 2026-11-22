package pe.medigest.farmacia.tiempo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** El reloj real del computador. */
public class RelojSistema implements Reloj {
    @Override
    public LocalDate hoy() { return LocalDate.now(); }

    @Override
    public LocalDateTime ahora() { return LocalDateTime.now(); }
}
