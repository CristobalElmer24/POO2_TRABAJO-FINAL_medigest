package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.TicketFarmacia;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Lleva la lista de observadores y les avisa; si uno falla, los demás igual reciben el aviso. */
public class NotificadorDispensacion {

    private final List<ObservadorDispensacion> observadores = new CopyOnWriteArrayList<>();

    public void registrar(ObservadorDispensacion o) { observadores.add(o); }

    public void quitar(ObservadorDispensacion o) { observadores.remove(o); }

    public void notificarDispensado(TicketFarmacia ticket) {
        for (ObservadorDispensacion o : observadores) {
            try {
                o.dispensado(ticket);
            } catch (RuntimeException e) {
                System.err.println("Un observador de dispensación falló: " + e.getMessage());
            }
        }
    }

    public void notificarAnulado(TicketFarmacia ticket) {
        for (ObservadorDispensacion o : observadores) {
            try {
                o.anulado(ticket);
            } catch (RuntimeException e) {
                System.err.println("Un observador de dispensación falló: " + e.getMessage());
            }
        }
    }

    public int cantidad() { return observadores.size(); }
}
