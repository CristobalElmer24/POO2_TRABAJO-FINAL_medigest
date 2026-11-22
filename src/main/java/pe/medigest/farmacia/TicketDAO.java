package pe.medigest.farmacia;

import java.util.List;

public interface TicketDAO {
    /** Guarda el ticket con sus detalles y le asigna el id. */
    void insertar(TicketFarmacia ticket);

    TicketFarmacia buscarPorId(int id);

    void actualizarEstado(int ticketId, String estado);

    List<TicketFarmacia> listarPorReceta(String codigoReceta);

    /** Busca por nombre o documento del paciente (coincidencia parcial). */
    List<TicketFarmacia> listarPorPaciente(String texto);

    List<TicketFarmacia> listarUltimos(int cantidad);
}
