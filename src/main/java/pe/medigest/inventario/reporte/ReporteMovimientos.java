package pe.medigest.inventario.reporte;

import pe.medigest.inventario.MovimientoInventario;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lista de movimientos más un total de unidades por tipo (entradas, salidas y ajustes). */
public class ReporteMovimientos {

    public TablaReporte generar(List<MovimientoInventario> movimientos) {
        TablaReporte tabla = new TablaReporte("MOVIMIENTOS DE INVENTARIO",
                Arrays.asList("Fecha", "Tipo", "Lote", "Cantidad", "Motivo"));
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (MovimientoInventario m : movimientos) {
            tabla.agregarFila(m.getFecha().format(formato), m.getTipo(),
                    m.getDetalleLote() != null ? m.getDetalleLote() : String.valueOf(m.getLoteId()),
                    String.valueOf(m.getCantidad()), m.getMotivo());
        }
        return tabla;
    }

    /** Suma de unidades por tipo de movimiento. */
    public Map<String, Integer> totalesPorTipo(List<MovimientoInventario> movimientos) {
        Map<String, Integer> totales = new LinkedHashMap<>();
        totales.put(MovimientoInventario.ENTRADA, 0);
        totales.put(MovimientoInventario.SALIDA, 0);
        totales.put(MovimientoInventario.AJUSTE, 0);
        for (MovimientoInventario m : movimientos) {
            totales.merge(m.getTipo(), m.getCantidad(), Integer::sum);
        }
        return totales;
    }
}
