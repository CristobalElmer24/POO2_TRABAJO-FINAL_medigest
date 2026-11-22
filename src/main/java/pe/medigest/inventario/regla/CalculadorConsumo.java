package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.MovimientoInventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Calcula cuántas unidades de un producto salieron en una ventana de días, a partir de los movimientos. */
public class CalculadorConsumo {

    private final int ventanaDias;

    public CalculadorConsumo(int ventanaDias) {
        this.ventanaDias = Math.max(1, ventanaDias);
    }

    /**
     * @param lotesDelProducto lotes del producto (para saber qué movimientos le pertenecen)
     * @param movimientos      movimientos recientes
     */
    public int unidadesConsumidas(List<LoteAlmacen> lotesDelProducto, List<MovimientoInventario> movimientos, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(ventanaDias - 1L);
        int total = 0;
        for (MovimientoInventario m : movimientos) {
            if (!MovimientoInventario.SALIDA.equals(m.getTipo())) {
                continue;
            }
            LocalDate dia = m.getFecha().toLocalDate();
            if (dia.isBefore(desde) || dia.isAfter(hoy)) {
                continue;
            }
            for (LoteAlmacen lote : lotesDelProducto) {
                if (lote.getId() == m.getLoteId()) {
                    total += m.getCantidad();
                    break;
                }
            }
        }
        return total;
    }

    public double promedioDiario(List<LoteAlmacen> lotesDelProducto, List<MovimientoInventario> movimientos, LocalDate hoy) {
        return unidadesConsumidas(lotesDelProducto, movimientos, hoy) / (double) ventanaDias;
    }

    public int getVentanaDias() { return ventanaDias; }
}
