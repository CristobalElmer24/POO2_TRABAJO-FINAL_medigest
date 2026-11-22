package pe.medigest.inventario.regla;

import pe.medigest.inventario.MovimientoInventario;
import pe.medigest.inventario.Producto;

import java.time.LocalDate;
import java.util.List;

/** Pide lo que se va a gastar en los próximos N días según el consumo reciente, menos lo que ya hay. */
public class ReposicionPorConsumo implements PoliticaReposicion {

    private final CalculadorConsumo calculador;
    private final List<MovimientoInventario> movimientos;
    private final int diasCobertura;
    private final LocalDate hoy;

    public ReposicionPorConsumo(CalculadorConsumo calculador, List<MovimientoInventario> movimientos,
                                int diasCobertura, LocalDate hoy) {
        this.calculador = calculador;
        this.movimientos = movimientos;
        this.diasCobertura = diasCobertura;
        this.hoy = hoy;
    }

    @Override
    public SugerenciaReposicion sugerir(Producto producto) {
        double diario = calculador.promedioDiario(producto.getLotes(), movimientos, hoy);
        int necesario = (int) Math.ceil(diario * diasCobertura);
        int faltante = Math.max(0, necesario - producto.stockUtilizable());
        String motivo = String.format("consumo %.1f/día, cobertura %d días", diario, diasCobertura);
        return new SugerenciaReposicion(producto.getId(), producto.getNombre(), faltante, motivo);
    }
}
