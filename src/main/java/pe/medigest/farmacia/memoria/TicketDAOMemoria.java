package pe.medigest.farmacia.memoria;

import pe.medigest.farmacia.TicketDAO;
import pe.medigest.farmacia.TicketFarmacia;

import java.util.ArrayList;
import java.util.List;

/** DAO de tickets en memoria. */
public class TicketDAOMemoria implements TicketDAO {

    private final List<TicketFarmacia> tickets = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public synchronized void insertar(TicketFarmacia t) {
        t.setId(siguienteId++);
        tickets.add(t);
    }

    @Override
    public synchronized TicketFarmacia buscarPorId(int id) {
        for (TicketFarmacia t : tickets) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    @Override
    public synchronized void actualizarEstado(int ticketId, String estado) {
        TicketFarmacia t = buscarPorId(ticketId);
        if (t != null && TicketFarmacia.ANULADO.equals(estado) && !TicketFarmacia.ANULADO.equals(t.getEstado())) {
            t.anular();
        }
    }

    @Override
    public synchronized List<TicketFarmacia> listarPorReceta(String codigoReceta) {
        List<TicketFarmacia> r = new ArrayList<>();
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).getRecetaCodigo().equalsIgnoreCase(codigoReceta)) {
                r.add(tickets.get(i));
            }
        }
        return r;
    }

    @Override
    public synchronized List<TicketFarmacia> listarPorPaciente(String texto) {
        String buscado = texto.toLowerCase();
        List<TicketFarmacia> r = new ArrayList<>();
        for (int i = tickets.size() - 1; i >= 0; i--) {
            if (tickets.get(i).getPacienteNombre().toLowerCase().contains(buscado)) {
                r.add(tickets.get(i));
            }
        }
        return r;
    }

    @Override
    public synchronized List<TicketFarmacia> listarUltimos(int cantidad) {
        List<TicketFarmacia> r = new ArrayList<>();
        for (int i = tickets.size() - 1; i >= 0 && r.size() < cantidad; i--) {
            r.add(tickets.get(i));
        }
        return r;
    }
}
