package pe.medigest.farmacia.reporte;

import pe.medigest.farmacia.TicketFarmacia;
import pe.medigest.inventario.reporte.FormateadorCSV;
import pe.medigest.inventario.reporte.FormateadorReporte;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Exporta el historial de tickets a un archivo (CSV por defecto). */
public class ExportadorTickets {

    private final FormateadorReporte formateador;
    private final ReporteDispensaciones reporte = new ReporteDispensaciones();

    public ExportadorTickets(FormateadorReporte formateador) {
        this.formateador = formateador;
    }

    public ExportadorTickets() {
        this(new FormateadorCSV());
    }

    public String generarTexto(List<TicketFarmacia> tickets) {
        return formateador.formatear(reporte.historial(tickets));
    }

    public void guardar(List<TicketFarmacia> tickets, Path destino) throws IOException {
        Files.write(destino, generarTexto(tickets).getBytes(StandardCharsets.UTF_8));
    }
}
