package pe.medigest.farmacia.reporte;

import pe.medigest.farmacia.TicketFarmacia;
import pe.medigest.inventario.reporte.TablaReporte;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/** Arma las tablas de los reportes de farmacia (se pueden dar formato con los formateadores de inventario). */
public class ReporteDispensaciones {

    public TablaReporte historial(List<TicketFarmacia> tickets) {
        TablaReporte tabla = new TablaReporte("HISTORIAL DE DISPENSACIONES",
                Arrays.asList("Ticket", "Fecha", "Receta", "Paciente", "Atendido por", "Unidades", "Estado"));
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (TicketFarmacia t : tickets) {
            tabla.agregarFila(String.valueOf(t.getId()), t.getFechaHora().format(formato), t.getRecetaCodigo(),
                    t.getPacienteNombre(), t.getUsuarioNombre(), String.valueOf(t.totalUnidades()), t.getEstado());
        }
        return tabla;
    }

    public TablaReporte masDispensados(List<TicketFarmacia> tickets, int n) {
        TablaReporte tabla = new TablaReporte("MEDICAMENTOS MÁS DISPENSADOS", Arrays.asList("Medicamento", "Unidades"));
        for (ProductoDispensado p : new EstadisticasFarmacia(tickets).masDispensados(n)) {
            tabla.agregarFila(p.getNombre(), String.valueOf(p.getUnidades()));
        }
        return tabla;
    }

    public TablaReporte resumenDiario(List<TicketFarmacia> tickets) {
        TablaReporte tabla = new TablaReporte("RESUMEN DIARIO", Arrays.asList("Fecha", "Dispensados", "Anulados", "Unidades"));
        for (ResumenDiario r : new EstadisticasFarmacia(tickets).resumenesPorDia()) {
            tabla.agregarFila(r.getFecha().toString(), String.valueOf(r.getDispensados()),
                    String.valueOf(r.getAnulados()), String.valueOf(r.getUnidades()));
        }
        return tabla;
    }
}
