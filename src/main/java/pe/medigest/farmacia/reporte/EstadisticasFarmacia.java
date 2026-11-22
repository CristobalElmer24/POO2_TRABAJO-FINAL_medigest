package pe.medigest.farmacia.reporte;

import pe.medigest.farmacia.DetalleTicket;
import pe.medigest.farmacia.TicketFarmacia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.LocalDate;

/** Cálculos sobre una lista de tickets. Los anulados no cuentan como unidades entregadas. */
public class EstadisticasFarmacia {

    private final List<TicketFarmacia> tickets;

    public EstadisticasFarmacia(List<TicketFarmacia> tickets) {
        this.tickets = new ArrayList<>(tickets);
    }

    public int totalTickets() { return tickets.size(); }

    public int ticketsAnulados() {
        int n = 0;
        for (TicketFarmacia t : tickets) {
            if (TicketFarmacia.ANULADO.equals(t.getEstado())) {
                n++;
            }
        }
        return n;
    }

    public int unidadesDispensadas() {
        int total = 0;
        for (TicketFarmacia t : tickets) {
            if (!TicketFarmacia.ANULADO.equals(t.getEstado())) {
                total += t.totalUnidades();
            }
        }
        return total;
    }

    /** Los "n" medicamentos con más unidades entregadas, de mayor a menor. */
    public List<ProductoDispensado> masDispensados(int n) {
        Map<String, Integer> porNombre = new LinkedHashMap<>();
        for (TicketFarmacia t : tickets) {
            if (TicketFarmacia.ANULADO.equals(t.getEstado())) {
                continue;
            }
            for (DetalleTicket d : t.getDetalles()) {
                porNombre.merge(d.getNombreProducto(), d.getCantidad(), Integer::sum);
            }
        }
        List<ProductoDispensado> lista = new ArrayList<>();
        for (Map.Entry<String, Integer> e : porNombre.entrySet()) {
            lista.add(new ProductoDispensado(e.getKey(), e.getValue()));
        }
        lista.sort((a, b) -> b.getUnidades() != a.getUnidades()
                ? Integer.compare(b.getUnidades(), a.getUnidades()) : a.getNombre().compareTo(b.getNombre()));
        return lista.size() > n ? new ArrayList<>(lista.subList(0, n)) : lista;
    }

    /** Un resumen por día, del más antiguo al más reciente. */
    public List<ResumenDiario> resumenesPorDia() {
        Map<LocalDate, ResumenDiario> porDia = new TreeMap<>();
        for (TicketFarmacia t : tickets) {
            ResumenDiario r = porDia.computeIfAbsent(t.getFechaHora().toLocalDate(), ResumenDiario::new);
            if (TicketFarmacia.ANULADO.equals(t.getEstado())) {
                r.sumarAnulado();
            } else {
                r.sumarDispensado(t.totalUnidades());
            }
        }
        return new ArrayList<>(porDia.values());
    }

    /** Cuántos tickets atendió cada usuario. */
    public Map<String, Integer> ticketsPorUsuario() {
        Map<String, Integer> mapa = new TreeMap<>();
        for (TicketFarmacia t : tickets) {
            mapa.merge(t.getUsuarioNombre(), 1, Integer::sum);
        }
        return mapa;
    }
}
