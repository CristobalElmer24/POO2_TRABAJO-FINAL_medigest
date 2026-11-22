package pe.medigest.farmacia.modelo;

import pe.medigest.farmacia.TicketFarmacia;

/** Estados posibles de un ticket de farmacia. */
public enum EstadoTicket {
    DISPENSADO(TicketFarmacia.DISPENSADO),
    ANULADO(TicketFarmacia.ANULADO);

    private final String codigo;

    EstadoTicket(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }

    public static EstadoTicket desde(String texto) {
        for (EstadoTicket e : values()) {
            if (e.codigo.equalsIgnoreCase(texto)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de ticket desconocido: " + texto);
    }
}
