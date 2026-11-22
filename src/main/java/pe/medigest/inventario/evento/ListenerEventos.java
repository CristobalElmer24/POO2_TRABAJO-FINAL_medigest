package pe.medigest.inventario.evento;

/** Quien quiera enterarse de los eventos del inventario implementa esto y se suscribe al bus. */
@FunctionalInterface
public interface ListenerEventos {
    void alOcurrir(EventoInventario evento);
}
