package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.DetalleTicket;
import pe.medigest.farmacia.TicketFarmacia;

/** Una línea por lote entregado: ticket, receta, medicamento, lote y cantidad (para abrir en Excel). */
public class ConstanciaCSV implements ConstanciaFormateador {

    @Override
    public String formatear(TicketFarmacia t) {
        StringBuilder sb = new StringBuilder("ticket,receta,paciente,medicamento,lote,cantidad\n");
        for (DetalleTicket d : t.getDetalles()) {
            sb.append(t.getId()).append(',')
                    .append(escapar(t.getRecetaCodigo())).append(',')
                    .append(escapar(t.getPacienteNombre())).append(',')
                    .append(escapar(d.getNombreProducto())).append(',')
                    .append(escapar(d.getNroLote())).append(',')
                    .append(d.getCantidad()).append('\n');
        }
        return sb.toString();
    }

    private String escapar(String s) {
        return (s.contains(",") || s.contains("\"")) ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
}
