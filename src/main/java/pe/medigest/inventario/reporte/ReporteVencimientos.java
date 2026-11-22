package pe.medigest.inventario.reporte;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.modelo.EstadoLote;
import pe.medigest.inventario.regla.ClasificadorEstadoLote;
import pe.medigest.inventario.regla.ComparadoresLote;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Lotes con stock que están vencidos o por vencer, del que vence antes al que vence después. */
public class ReporteVencimientos {

    private final ClasificadorEstadoLote clasificador;

    public ReporteVencimientos(int diasPorVencer) {
        this.clasificador = new ClasificadorEstadoLote(diasPorVencer);
    }

    public TablaReporte generar(List<Producto> productos, LocalDate hoy) {
        TablaReporte tabla = new TablaReporte("LOTES VENCIDOS Y POR VENCER",
                Arrays.asList("Producto", "Lote", "Vence", "Días", "Unidades", "Estado"));
        List<LoteAlmacen> todos = new ArrayList<>();
        List<String> nombres = new ArrayList<>();
        for (Producto p : productos) {
            for (LoteAlmacen l : p.getLotes()) {
                todos.add(l);
                nombres.add(p.getNombre());
            }
        }
        List<Integer> orden = new ArrayList<>();
        for (int i = 0; i < todos.size(); i++) {
            EstadoLote estado = clasificador.clasificar(todos.get(i), hoy);
            if (estado == EstadoLote.VENCIDO || estado == EstadoLote.POR_VENCER) {
                if (todos.get(i).getCantidadDisponible() > 0) {
                    orden.add(i);
                }
            }
        }
        orden.sort((a, b) -> ComparadoresLote.porVencimiento().compare(todos.get(a), todos.get(b)));
        for (int i : orden) {
            LoteAlmacen l = todos.get(i);
            long dias = ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento());
            tabla.agregarFila(nombres.get(i), l.getNroLote(), l.getFechaVencimiento().toString(),
                    String.valueOf(dias), String.valueOf(l.getCantidadDisponible()),
                    clasificador.clasificar(l, hoy).name());
        }
        return tabla;
    }
}
