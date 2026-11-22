package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.TicketFarmacia;

/** Strategy: arma la constancia de un ticket en un formato concreto. */
public interface ConstanciaFormateador {
    String formatear(TicketFarmacia ticket);
}
