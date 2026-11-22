package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.TicketFarmacia;

/** Observer: se entera cuando se dispensa o se anula un ticket (después de que la operación se confirmó). */
public interface ObservadorDispensacion {
    void dispensado(TicketFarmacia ticket);

    default void anulado(TicketFarmacia ticket) { }
}
