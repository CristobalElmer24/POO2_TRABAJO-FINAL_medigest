package pe.medigest.inventario.evento;

import java.time.LocalDateTime;

/** Hecho que ocurrió en el inventario (base de los eventos concretos). */
public abstract class EventoInventario {

    private final LocalDateTime fecha = LocalDateTime.now();

    public LocalDateTime getFecha() { return fecha; }

    /** Texto corto para mostrar en una lista o en un log. */
    public abstract String descripcion();

    @Override
    public String toString() {
        return descripcion();
    }
}
