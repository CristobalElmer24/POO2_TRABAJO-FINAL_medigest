package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.MovimientoAjuste;
import pe.medigest.inventario.MovimientoFactory;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.StockInsuficienteException;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoteYMovimientosTest {

    private final LocalDate hoy = LocalDate.now();

    @Test
    void descontarYAgregarCambianLaCantidad() {
        LoteAlmacen lote = new LoteAlmacen(1, 1, "A", hoy.plusDays(50), 10);
        lote.descontar(4);
        assertEquals(6, lote.getCantidadDisponible());
        lote.agregar(2);
        assertEquals(8, lote.getCantidadDisponible());
    }

    @Test
    void noSePuedeDescontarMasDeLoQueHay() {
        LoteAlmacen lote = new LoteAlmacen(1, 1, "A", hoy.plusDays(50), 3);
        assertThrows(StockInsuficienteException.class, () -> lote.descontar(4));
    }

    @Test
    void unLoteQueVenceHoyTodaviaNoEstaVencido() {
        assertFalse(new LoteAlmacen(1, 1, "A", hoy, 1).estaVencido());
        assertTrue(new LoteAlmacen(1, 1, "A", hoy.minusDays(1), 1).estaVencido());
    }

    @Test
    void laFabricaCreaCadaTipoDeMovimiento() {
        LoteAlmacen lote = new LoteAlmacen(1, 1, "A", hoy.plusDays(50), 10);
        MovimientoFactory.crear("ENTRADA", 1, 5, "ingreso").aplicar(lote);
        assertEquals(15, lote.getCantidadDisponible());
        MovimientoFactory.crear("salida", 1, 3, "dispensación").aplicar(lote);
        assertEquals(12, lote.getCantidadDisponible());
        assertTrue(MovimientoFactory.crear("AJUSTE", 1, -2, "merma") instanceof MovimientoAjuste);
    }

    @Test
    void laFabricaRechazaTiposYCantidadesInvalidas() {
        assertThrows(ReglaNegocioException.class, () -> MovimientoFactory.crear("OTRO", 1, 1, "x"));
        assertThrows(ReglaNegocioException.class, () -> MovimientoFactory.crear("SALIDA", 1, 0, "x"));
        assertThrows(ReglaNegocioException.class, () -> MovimientoFactory.crear("AJUSTE", 1, 3, " "));
    }

    @Test
    void elStockUtilizableNoCuentaLotesVencidos() {
        Producto p = new Producto(1, "P001", "Paracetamol", "500 mg", 20);
        p.setLotes(Arrays.asList(
                new LoteAlmacen(1, 1, "A", hoy.plusDays(100), 10),
                new LoteAlmacen(2, 1, "B", hoy.minusDays(5), 50)));
        assertEquals(60, p.stockTotal());
        assertEquals(10, p.stockUtilizable());
    }

    @Test
    void elProductoSeValidaAntesDeGuardarse() {
        assertThrows(ReglaNegocioException.class, () -> new Producto("", "Nombre", "5 mg", 1).validar());
        assertThrows(ReglaNegocioException.class, () -> new Producto("P1", "Nombre", "5 mg", -1).validar());
    }
}
