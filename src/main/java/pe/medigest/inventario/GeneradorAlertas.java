package pe.medigest.inventario;

import pe.medigest.inventario.regla.ConfiguracionInventario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula las alertas (stock bajo, lote por vencer, lote vencido) a partir de una lista de productos.
 * No toca la base de datos: ServicioInventario le pasa los productos ya cargados.
 */
public class GeneradorAlertas {

    private final ConfiguracionInventario config;

    public GeneradorAlertas(ConfiguracionInventario config) {
        this.config = config;
    }

    public List<AlertaInventario> generar(List<Producto> productos, LocalDate hoy) {
        List<AlertaInventario> alertas = new ArrayList<>();
        for (Producto p : productos) {
            int utilizable = p.stockUtilizable();
            if (utilizable <= p.getStockMinimo()) {
                alertas.add(AlertaFactory.stockBajo(p, utilizable));
            }
            for (LoteAlmacen lote : p.getLotes()) {
                if (lote.getCantidadDisponible() <= 0) {
                    continue;
                }
                if (lote.estaVencido(hoy)) {
                    alertas.add(AlertaFactory.loteVencido(p, lote));
                } else if (lote.estaPorVencer(config.getDiasAlertaVencimiento())) {
                    long dias = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
                    alertas.add(AlertaFactory.porVencer(p, lote, dias));
                }
            }
        }
        return alertas;
    }
}
