package pe.medigest.inventario;

/** PATRÓN: Observer. Las vistas se suscriben al ServicioInventario y reciben las alertas cuando aparecen. */
public interface ObservadorAlertas {

    /** Se llama cuando hay una alerta nueva o cuando una alerta existente cambió su mensaje. */
    void actualizar(AlertaInventario alerta);

    /** Se llama cuando una alerta dejó de aplicar (por ejemplo, se repuso el stock). */
    default void alertaResuelta(String claveAlerta) { }
}
