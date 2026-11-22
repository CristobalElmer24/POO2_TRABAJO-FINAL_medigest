package pe.medigest.inventario.evento;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer sobre eventos: publica cada evento a todos los suscritos.
 * Un listener que falle no impide que los demás reciban el evento.
 */
public class BusEventosInventario {

    private final List<ListenerEventos> listeners = new CopyOnWriteArrayList<>();

    public void suscribir(ListenerEventos listener) {
        listeners.add(listener);
    }

    public void desuscribir(ListenerEventos listener) {
        listeners.remove(listener);
    }

    public void publicar(EventoInventario evento) {
        for (ListenerEventos l : listeners) {
            try {
                l.alOcurrir(evento);
            } catch (RuntimeException e) {
                System.err.println("Un listener de eventos falló: " + e.getMessage());
            }
        }
    }

    public int cantidadSuscritos() { return listeners.size(); }
}
