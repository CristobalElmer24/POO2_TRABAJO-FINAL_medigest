package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.AlertaInventario;
import pe.medigest.inventario.BuscadorProductos;
import pe.medigest.inventario.EstrategiaSeleccionLote;
import pe.medigest.inventario.GeneradorAlertas;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.MovimientoInventario;
import pe.medigest.inventario.MovimientoSalida;
import pe.medigest.inventario.PlanDescuento;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.ProductoBuilder;
import pe.medigest.inventario.evento.BusEventosInventario;
import pe.medigest.inventario.evento.EventoLoteIngresado;
import pe.medigest.inventario.evento.RegistroEventos;
import pe.medigest.inventario.excepcion.AjusteInvalidoException;
import pe.medigest.inventario.excepcion.CantidadInvalidaException;
import pe.medigest.inventario.excepcion.CodigoProductoInvalidoException;
import pe.medigest.inventario.memoria.LoteDAOMemoria;
import pe.medigest.inventario.memoria.ProductoDAOMemoria;
import pe.medigest.inventario.modelo.Cantidad;
import pe.medigest.inventario.modelo.CodigoProducto;
import pe.medigest.inventario.modelo.EstadoLote;
import pe.medigest.inventario.modelo.NivelStock;
import pe.medigest.inventario.modelo.RangoFechas;
import pe.medigest.inventario.regla.CalculadorConsumo;
import pe.medigest.inventario.regla.ClasificadorEstadoLote;
import pe.medigest.inventario.regla.ClasificadorNivelStock;
import pe.medigest.inventario.regla.ConfiguracionInventario;
import pe.medigest.inventario.regla.FabricaEstrategias;
import pe.medigest.inventario.regla.LoteConStock;
import pe.medigest.inventario.regla.LotePorVencer;
import pe.medigest.inventario.regla.LoteVigente;
import pe.medigest.inventario.regla.ReposicionPorConsumo;
import pe.medigest.inventario.regla.ReposicionPorMinimo;
import pe.medigest.inventario.regla.SeleccionMenorCantidad;
import pe.medigest.inventario.regla.SugerenciaReposicion;
import pe.medigest.inventario.regla.ValidadorAjuste;
import pe.medigest.inventario.reporte.FormateadorCSV;
import pe.medigest.inventario.reporte.FormateadorTexto;
import pe.medigest.inventario.reporte.ReporteStock;
import pe.medigest.inventario.reporte.ReporteVencimientos;
import pe.medigest.inventario.reporte.TablaReporte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventarioNuevasClasesTest {

    private final LocalDate hoy = LocalDate.of(2026, 11, 20);

    private LoteAlmacen lote(int id, String nro, int dias, int cantidad) {
        return new LoteAlmacen(id, 1, nro, hoy.plusDays(dias), cantidad);
    }

    // ---------------------------------------------------------------- valores
    @Test
    void codigoDeProductoSeNormalizaYValida() {
        assertEquals("PARA-01", CodigoProducto.de("  para-01 ").getValor());
        assertThrows(CodigoProductoInvalidoException.class, () -> CodigoProducto.de("a"));
        assertThrows(CodigoProductoInvalidoException.class, () -> CodigoProducto.de("  "));
        assertThrows(CodigoProductoInvalidoException.class, () -> CodigoProducto.de("pa ra"));
    }

    @Test
    void cantidadNoPermiteNegativos() {
        assertEquals(8, Cantidad.de(5).sumar(Cantidad.de(3)).getUnidades());
        assertThrows(CantidadInvalidaException.class, () -> Cantidad.de(2).restar(Cantidad.de(3)));
        assertThrows(CantidadInvalidaException.class, () -> Cantidad.de(-1));
        assertThrows(CantidadInvalidaException.class, () -> Cantidad.positiva(0));
        assertTrue(Cantidad.CERO.esCero());
    }

    @Test
    void rangoDeFechasIncluyeLosExtremos() {
        RangoFechas r = new RangoFechas(hoy, hoy.plusDays(10));
        assertTrue(r.contiene(hoy));
        assertTrue(r.contiene(hoy.plusDays(10)));
        assertFalse(r.contiene(hoy.plusDays(11)));
        assertEquals(11, r.dias());
        assertThrows(ReglaNegocioException.class, () -> new RangoFechas(hoy, hoy.minusDays(1)));
    }

    // ---------------------------------------------------------------- clasificadores
    @Test
    void clasificaElEstadoDeUnLote() {
        ClasificadorEstadoLote c = new ClasificadorEstadoLote(30);
        assertEquals(EstadoLote.VIGENTE, c.clasificar(lote(1, "A", 200, 5), hoy));
        assertEquals(EstadoLote.POR_VENCER, c.clasificar(lote(2, "B", 10, 5), hoy));
        assertEquals(EstadoLote.POR_VENCER, c.clasificar(lote(3, "C", 0, 5), hoy)); // vence hoy: todavía sirve
        assertEquals(EstadoLote.VENCIDO, c.clasificar(lote(4, "D", -1, 5), hoy));
        assertEquals(EstadoLote.AGOTADO, c.clasificar(lote(5, "E", 200, 0), hoy));
        assertTrue(EstadoLote.POR_VENCER.esDispensable());
        assertFalse(EstadoLote.VENCIDO.esDispensable());
    }

    @Test
    void clasificaElNivelDeStock() {
        ClasificadorNivelStock c = new ClasificadorNivelStock();
        assertEquals(NivelStock.SIN_STOCK, c.clasificar(0, 10));
        assertEquals(NivelStock.CRITICO, c.clasificar(5, 10));
        assertEquals(NivelStock.BAJO, c.clasificar(10, 10));
        assertEquals(NivelStock.NORMAL, c.clasificar(20, 10));
        assertEquals(NivelStock.ALTO, c.clasificar(21, 10));
        assertTrue(NivelStock.BAJO.requiereReposicion());
        assertFalse(NivelStock.NORMAL.requiereReposicion());
    }

    // ---------------------------------------------------------------- especificaciones
    @Test
    void lasEspecificacionesSeCombinan() {
        List<LoteAlmacen> lotes = Arrays.asList(lote(1, "A", 200, 5), lote(2, "B", 10, 5),
                lote(3, "C", -3, 40), lote(4, "D", 15, 0));
        assertEquals(3, new LoteVigente(hoy).filtrar(lotes).size());
        assertEquals(2, new LoteVigente(hoy).y(new LoteConStock()).filtrar(lotes).size());
        List<LoteAlmacen> porVencer = new LotePorVencer(30, hoy).y(new LoteConStock()).filtrar(lotes);
        assertEquals(1, porVencer.size());
        assertEquals("B", porVencer.get(0).getNroLote());
        assertEquals(1, new LoteVigente(hoy).no().filtrar(lotes).size());
        assertEquals(4, new LoteVigente(hoy).o(new LoteConStock()).filtrar(lotes).size());
    }

    // ---------------------------------------------------------------- estrategias
    @Test
    void menorCantidadVaciaPrimeroElLoteMasChico() {
        LoteAlmacen grande = lote(1, "G", 300, 50);
        LoteAlmacen chico = lote(2, "C", 400, 3);
        List<LoteAlmacen> elegidos = new SeleccionMenorCantidad().seleccionar(Arrays.asList(grande, chico), 5);
        assertSame(chico, elegidos.get(0));
        assertSame(grande, elegidos.get(1));
    }

    @Test
    void laFabricaDevuelveLaEstrategiaPorNombre() {
        assertTrue(FabricaEstrategias.crear("fefo").getNombre().startsWith("FEFO"));
        assertTrue(FabricaEstrategias.crear(" FIFO ").getNombre().startsWith("FIFO"));
        assertEquals("Menor cantidad primero", FabricaEstrategias.crear("menor").getNombre());
        assertThrows(ReglaNegocioException.class, () -> FabricaEstrategias.crear("LIFO"));
        assertEquals(3, FabricaEstrategias.nombresDisponibles().size());
    }

    @Test
    void elPlanDeDescuentoSimulaSinTocarLosLotes() {
        LoteAlmacen a = lote(1, "A", 100, 10);
        LoteAlmacen b = lote(2, "B", 10, 5);
        EstrategiaSeleccionLote fefo = FabricaEstrategias.crear("FEFO");
        PlanDescuento plan = PlanDescuento.simular(fefo, Arrays.asList(a, b), 8);
        assertEquals(2, plan.cantidadLotes());
        assertEquals(8, plan.totalUnidades());
        assertEquals("B", plan.getPasos().get(0).getLote().getNroLote());
        assertEquals(5, plan.getPasos().get(0).getCantidad());
        assertEquals(3, plan.getPasos().get(1).getCantidad());
        assertEquals(10, a.getCantidadDisponible());
        assertEquals(5, b.getCantidadDisponible());
    }

    // ---------------------------------------------------------------- validación de ajustes
    @Test
    void elAjusteNoPuedeDejarElLoteEnNegativo() {
        ValidadorAjuste v = new ValidadorAjuste();
        LoteAlmacen l = lote(1, "A", 100, 4);
        v.validar(l, -4, "Conteo físico");
        assertThrows(AjusteInvalidoException.class, () -> v.validar(l, -5, "Merma"));
        assertThrows(AjusteInvalidoException.class, () -> v.validar(l, 0, "Nada"));
        assertThrows(AjusteInvalidoException.class, () -> v.validar(l, 3, " "));
    }

    // ---------------------------------------------------------------- reposición
    private Producto producto(int id, String nombre, int minimo, LoteAlmacen... lotes) {
        Producto p = new Producto(id, "COD-" + id, nombre, "Caja", minimo);
        for (LoteAlmacen l : lotes) {
            p.agregarLote(l);
        }
        return p;
    }

    @Test
    void reposicionPorMinimoPideHastaElDobleDelMinimo() {
        Producto bajo = producto(1, "Paracetamol", 20, lote(1, "A", 100, 5));
        SugerenciaReposicion s = new ReposicionPorMinimo().sugerir(bajo);
        assertEquals(35, s.getUnidadesSugeridas()); // 40 - 5
        assertTrue(s.hayQueReponer());
        Producto bien = producto(2, "Ibuprofeno", 20, lote(2, "B", 100, 50));
        assertFalse(new ReposicionPorMinimo().sugerir(bien).hayQueReponer());
    }

    @Test
    void reposicionPorConsumoUsaLasSalidasRecientes() {
        LoteAlmacen l = lote(1, "A", 100, 10);
        Producto p = producto(1, "Paracetamol", 5, l);
        MovimientoSalida s1 = new MovimientoSalida(1, 30, "Dispensación");
        s1.setFecha(hoy.minusDays(3).atStartOfDay());
        MovimientoSalida s2 = new MovimientoSalida(1, 30, "Dispensación");
        s2.setFecha(hoy.minusDays(60).atStartOfDay()); // fuera de la ventana
        List<MovimientoInventario> movs = Arrays.asList(s1, s2);
        CalculadorConsumo calc = new CalculadorConsumo(30);
        assertEquals(30, calc.unidadesConsumidas(p.getLotes(), movs, hoy));
        SugerenciaReposicion sug = new ReposicionPorConsumo(calc, movs, 30, hoy).sugerir(p);
        assertEquals(20, sug.getUnidadesSugeridas()); // necesita 30, tiene 10
    }

    // ---------------------------------------------------------------- alertas
    @Test
    void generaLasTresClasesDeAlerta() {
        Producto p = producto(7, "Amoxicilina", 20,
                lote(1, "VENCIDO", -5, 10), lote(2, "PRONTO", 10, 15), lote(3, "LARGO", 300, 2));
        List<AlertaInventario> alertas = new GeneradorAlertas(ConfiguracionInventario.porDefecto())
                .generar(Arrays.asList(p), hoy);
        List<String> claves = new ArrayList<>();
        for (AlertaInventario a : alertas) {
            claves.add(a.getClave());
        }
        assertEquals(3, alertas.size());
        assertTrue(claves.contains("STOCK-7"));   // utilizable 17 <= 20
        assertTrue(claves.contains("VENC-1"));
        assertTrue(claves.contains("POR-2"));
    }

    // ---------------------------------------------------------------- reportes
    @Test
    void elReporteDeStockOrdenaLoDeMayorUrgenciaPrimero() {
        Producto sinStock = producto(1, "Loratadina", 10, lote(1, "V", -2, 50));
        Producto alto = producto(2, "Paracetamol", 10, lote(2, "A", 400, 100));
        TablaReporte t = new ReporteStock().generar(Arrays.asList(alto, sinStock));
        assertEquals(2, t.cantidadFilas());
        assertEquals("Loratadina", t.getFilas().get(0).get(1));
        assertEquals("SIN_STOCK", t.getFilas().get(0).get(6));
        assertEquals("ALTO", t.getFilas().get(1).get(6));
    }

    @Test
    void elReporteDeVencimientosSoloListaVencidosYPorVencer() {
        Producto p = producto(1, "Paracetamol", 1,
                lote(1, "LARGO", 300, 5), lote(2, "PRONTO", 12, 5), lote(3, "VENC", -4, 5), lote(4, "VACIO", 3, 0));
        TablaReporte t = new ReporteVencimientos(30).generar(Arrays.asList(p), hoy);
        assertEquals(2, t.cantidadFilas());
        assertEquals("VENC", t.getFilas().get(0).get(1));
        assertEquals("PRONTO", t.getFilas().get(1).get(1));
    }

    @Test
    void losFormateadoresPresentanLaTabla() {
        TablaReporte t = new TablaReporte("Prueba", Arrays.asList("Nombre", "Nota"));
        t.agregarFila("Ana, la primera", "dijo \"hola\"");
        String csv = new FormateadorCSV().formatear(t);
        assertEquals("Nombre,Nota\n\"Ana, la primera\",\"dijo \"\"hola\"\"\"\n", csv);
        String texto = new FormateadorTexto().formatear(t);
        assertTrue(texto.startsWith("Prueba\n"));
        assertTrue(texto.contains("Ana, la primera"));
        assertThrows(IllegalArgumentException.class, () -> t.agregarFila("una sola"));
    }

    // ---------------------------------------------------------------- eventos
    @Test
    void elBusEntregaEventosAunqueUnListenerFalle() {
        BusEventosInventario bus = new BusEventosInventario();
        RegistroEventos registro = new RegistroEventos(2);
        bus.suscribir(e -> { throw new RuntimeException("falla a propósito"); });
        bus.suscribir(registro);
        bus.publicar(new EventoLoteIngresado(1, "A", 5));
        bus.publicar(new EventoLoteIngresado(1, "B", 6));
        bus.publicar(new EventoLoteIngresado(1, "C", 7));
        assertEquals(2, registro.ultimos().size());
        assertTrue(registro.ultimos().get(0).descripcion().contains("lote C"));
        assertEquals(2, bus.cantidadSuscritos());
    }

    // ---------------------------------------------------------------- utilidades y DAOs en memoria
    @Test
    void elBuscadorIgnoraTildesYMayusculas() {
        List<Producto> lista = Arrays.asList(producto(1, "Ácido fólico", 1), producto(2, "Paracetamol", 1));
        assertEquals(1, new BuscadorProductos().buscar(lista, "acido").size());
        assertEquals(1, new BuscadorProductos().buscar(lista, "PARACET").size());
        assertEquals(2, new BuscadorProductos().buscar(lista, "  ").size());
        assertEquals(0, new BuscadorProductos().buscar(lista, "zzz").size());
    }

    @Test
    void elBuilderValidaAlConstruir() {
        Producto p = new ProductoBuilder().conCodigo("para-1").conNombre("Paracetamol 500 mg").build();
        assertEquals("PARA-1", p.getCodigo());
        assertEquals(10, p.getStockMinimo());
        assertThrows(ReglaNegocioException.class, () -> new ProductoBuilder().conCodigo("OK-1").build());
        assertThrows(CodigoProductoInvalidoException.class, () -> new ProductoBuilder().conNombre("X").build());
    }

    @Test
    void losDaoEnMemoriaGuardanYBuscan() {
        ProductoDAOMemoria productos = new ProductoDAOMemoria();
        Producto p = new Producto("PARA-1", "Paracetamol", "Caja", 5);
        productos.insertar(p);
        assertEquals(1, p.getId());
        assertSame(p, productos.buscarPorCodigo("PARA-1"));
        assertNull(productos.buscarPorCodigo("NOPE"));

        LoteDAOMemoria lotes = new LoteDAOMemoria();
        LoteAlmacen l = new LoteAlmacen(0, p.getId(), "L1", hoy.plusDays(90), 10);
        lotes.insertar(l);
        lotes.actualizarCantidad(l.getId(), 0);
        assertEquals(0, lotes.buscarPorId(l.getId()).getCantidadDisponible());
        lotes.actualizarCantidad(l.getId(), 7);
        assertEquals(7, lotes.buscarPorProductoYNroLote(p.getId(), "L1").getCantidadDisponible());
        assertEquals(1, lotes.listarPorProducto(p.getId()).size());
        assertNotNull(lotes.buscarPorId(l.getId()));
        productos.eliminar(p.getId());
        assertEquals(0, productos.listar().size());
    }
}
