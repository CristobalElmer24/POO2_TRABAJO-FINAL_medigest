package pe.medigest.farmacia.tiempo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Fuente de la fecha y hora. Permite probar reglas de vigencia sin depender del día en que se ejecuta la prueba. */
public interface Reloj {
    LocalDate hoy();

    LocalDateTime ahora();
}
