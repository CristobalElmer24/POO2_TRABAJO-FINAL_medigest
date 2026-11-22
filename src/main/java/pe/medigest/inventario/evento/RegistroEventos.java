package pe.medigest.inventario.evento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Listener que guarda en memoria los últimos eventos (útil para mostrar un historial reciente y para pruebas). */
public class RegistroEventos implements ListenerEventos {

    private final int maximo;
    private final LinkedList<EventoInventario> eventos = new LinkedList<>();

    public RegistroEventos(int maximo) {
        this.maximo = Math.max(1, maximo);
    }

    @Override
    public synchronized void alOcurrir(EventoInventario evento) {
        eventos.addFirst(evento);
        while (eventos.size() > maximo) {
            eventos.removeLast();
        }
    }

    /** Del más reciente al más antiguo. */
    public synchronized List<EventoInventario> ultimos() {
        return Collections.unmodifiableList(new ArrayList<>(eventos));
    }
}
