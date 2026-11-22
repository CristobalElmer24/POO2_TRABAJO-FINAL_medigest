package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.DetalleTicket;
import pe.medigest.farmacia.TicketFarmacia;

import java.time.format.DateTimeFormatter;

/** Constancia de dispensación en texto plano, lista para mostrar en pantalla o imprimir. */
public class ConstanciaTexto implements ConstanciaFormateador {

    @Override
    public String formatear(TicketFarmacia t) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("CLÍNICA RICARDO PALMA - FARMACIA\n");
        sb.append("========================================\n");
        sb.append("Ticket N°:    ").append(t.getId()).append("\n");
        sb.append("Receta:       ").append(t.getRecetaCodigo()).append("\n");
        sb.append("Paciente:     ").append(t.getPacienteNombre()).append("\n");
        sb.append("Fecha:        ").append(t.getFechaHora().format(formato)).append("\n");
        sb.append("Atendido por: ").append(t.getUsuarioNombre()).append("\n");
        sb.append("Estado:       ").append(t.getEstado()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %-12s %6s%n", "Medicamento", "Lote", "Cant."));
        for (DetalleTicket d : t.getDetalles()) {
            sb.append(String.format("%-20s %-12s %6d%n", d.getNombreProducto(), d.getNroLote(), d.getCantidad()));
        }
        sb.append("----------------------------------------\n");
        sb.append("Total de unidades: ").append(t.totalUnidades()).append("\n");
        return sb.toString();
    }
}
