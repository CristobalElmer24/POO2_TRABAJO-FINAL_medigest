package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.DetalleTicket;
import pe.medigest.farmacia.TicketFarmacia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** El ticket de una dispensación agrupado por medicamento (el ticket los guarda por lote). */
public class ResultadoDispensacion {
    private final TicketFarmacia ticket;
    private final List<LineaDispensacion> lineas;

    private ResultadoDispensacion(TicketFarmacia ticket, List<LineaDispensacion> lineas) {
        this.ticket = ticket;
        this.lineas = lineas;
    }

    public static ResultadoDispensacion desde(TicketFarmacia ticket) {
        Map<Integer, LineaDispensacion> porProducto = new LinkedHashMap<>();
        for (DetalleTicket d : ticket.getDetalles()) {
            porProducto.computeIfAbsent(d.getProductoId(),
                    id -> new LineaDispensacion(id, d.getNombreProducto())).agregar(d);
        }
        return new ResultadoDispensacion(ticket, new ArrayList<>(porProducto.values()));
    }

    public TicketFarmacia getTicket() { return ticket; }

    public List<LineaDispensacion> getLineas() { return Collections.unmodifiableList(lineas); }

    public int totalUnidades() { return ticket.totalUnidades(); }

    /** True si algún medicamento tuvo que salir de más de un lote. */
    public boolean usoVariosLotes() {
        for (LineaDispensacion l : lineas) {
            if (l.getLotesUsados().size() > 1) {
                return true;
            }
        }
        return false;
    }
}
