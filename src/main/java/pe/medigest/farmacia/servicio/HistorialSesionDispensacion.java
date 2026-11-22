package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.TicketFarmacia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Observador que recuerda los últimos tickets movidos en esta sesión (dispensados o anulados). */
public class HistorialSesionDispensacion implements ObservadorDispensacion {

    private final int maximo;
    private final LinkedList<String> lineas = new LinkedList<>();

    public HistorialSesionDispensacion(int maximo) {
        this.maximo = Math.max(1, maximo);
    }

    @Override
    public synchronized void dispensado(TicketFarmacia t) {
        agregar("Dispensado ticket " + t.getId() + " (" + t.getRecetaCodigo() + ", " + t.totalUnidades() + " unid.)");
    }

    @Override
    public synchronized void anulado(TicketFarmacia t) {
        agregar("Anulado ticket " + t.getId() + " (" + t.getRecetaCodigo() + ")");
    }

    private void agregar(String linea) {
        lineas.addFirst(linea);
        while (lineas.size() > maximo) {
            lineas.removeLast();
        }
    }

    /** Del más reciente al más antiguo. */
    public synchronized List<String> getLineas() {
        return Collections.unmodifiableList(new ArrayList<>(lineas));
    }
}
