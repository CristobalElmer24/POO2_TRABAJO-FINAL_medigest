package pe.medigest.farmacia.reporte;

import pe.medigest.farmacia.TicketFarmacia;

import java.util.ArrayList;
import java.util.List;

/** Aplica un {@link FiltroTickets} a una lista de tickets. */
public class BuscadorTickets {

    public List<TicketFarmacia> buscar(List<TicketFarmacia> tickets, FiltroTickets filtro) {
        List<TicketFarmacia> resultado = new ArrayList<>();
        for (TicketFarmacia t : tickets) {
            if (filtro.cumple(t)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
