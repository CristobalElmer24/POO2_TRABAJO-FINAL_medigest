package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

import java.util.Comparator;

/** Órdenes de lotes que se repiten en reportes y estrategias. */
public final class ComparadoresLote {

    private ComparadoresLote() { }

    public static Comparator<LoteAlmacen> porVencimiento() {
        return Comparator.comparing(LoteAlmacen::getFechaVencimiento).thenComparingInt(LoteAlmacen::getId);
    }

    public static Comparator<LoteAlmacen> porIngreso() {
        return Comparator.comparingInt(LoteAlmacen::getId);
    }

    public static Comparator<LoteAlmacen> porCantidadAscendente() {
        return Comparator.comparingInt(LoteAlmacen::getCantidadDisponible).thenComparingInt(LoteAlmacen::getId);
    }
}
