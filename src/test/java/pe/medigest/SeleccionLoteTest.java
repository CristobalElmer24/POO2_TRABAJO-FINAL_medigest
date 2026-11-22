package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.LoteVencidoException;
import pe.medigest.inventario.SeleccionFEFO;
import pe.medigest.inventario.SeleccionFIFO;
import pe.medigest.inventario.StockInsuficienteException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeleccionLoteTest {

    private final LocalDate hoy = LocalDate.now();
    private final LoteAlmacen loteA = new LoteAlmacen(1, 1, "A", hoy.plusDays(100), 10);
    private final LoteAlmacen loteB = new LoteAlmacen(2, 1, "B", hoy.plusDays(10), 5);
    private final LoteAlmacen vencido = new LoteAlmacen(3, 1, "C", hoy.minusDays(1), 50);
    private final LoteAlmacen sinStock = new LoteAlmacen(4, 1, "D", hoy.plusDays(5), 0);
    private final List<LoteAlmacen> lotes = Arrays.asList(loteA, loteB, vencido, sinStock);

    @Test
    void fefoUsaPrimeroElLoteQueVenceAntes() {
        List<LoteAlmacen> elegidos = new SeleccionFEFO().seleccionar(lotes, 8);
        assertEquals(2, elegidos.size());
        assertSame(loteB, elegidos.get(0));
        assertSame(loteA, elegidos.get(1));
    }

    @Test
    void fefoNoTomaMasLotesDeLosNecesarios() {
        assertEquals(1, new SeleccionFEFO().seleccionar(lotes, 5).size());
    }

    @Test
    void fefoIgnoraLotesVencidosAunqueTenganStock() {
        // entre A y B hay 15 unidades vigentes; el lote vencido (50) no cuenta
        assertThrows(StockInsuficienteException.class, () -> new SeleccionFEFO().seleccionar(lotes, 16));
    }

    @Test
    void siSoloHayLotesVencidosSeAvisaEso() {
        assertThrows(LoteVencidoException.class, () -> new SeleccionFEFO().seleccionar(Arrays.asList(vencido), 1));
    }

    @Test
    void cantidadCeroONegativaSeRechaza() {
        assertThrows(ReglaNegocioException.class, () -> new SeleccionFEFO().seleccionar(lotes, 0));
    }

    @Test
    void fifoUsaPrimeroElLoteRegistradoAntes() {
        assertSame(loteA, new SeleccionFIFO().seleccionar(lotes, 8).get(0));
    }
}
