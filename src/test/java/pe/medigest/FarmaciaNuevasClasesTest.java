package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.farmacia.DetalleTicket;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaBuilder;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.TicketFarmacia;
import pe.medigest.farmacia.ValidadorReceta;
import pe.medigest.farmacia.excepcion.DocumentoInvalidoException;
import pe.medigest.farmacia.excepcion.RecetaNoEncontradaException;
import pe.medigest.farmacia.excepcion.RecetaVencidaException;
import pe.medigest.farmacia.excepcion.RecetaYaDispensadaException;
import pe.medigest.farmacia.memoria.RecetaDAOMemoria;
import pe.medigest.farmacia.memoria.TicketDAOMemoria;
import pe.medigest.farmacia.modelo.CodigoReceta;
import pe.medigest.farmacia.modelo.DocumentoPaciente;
import pe.medigest.farmacia.modelo.EstadoReceta;
import pe.medigest.farmacia.modelo.EstadoTicket;
import pe.medigest.farmacia.modelo.MotivoRechazo;
import pe.medigest.farmacia.modelo.TipoDocumento;
import pe.medigest.farmacia.regla.CadenaValidacionReceta;
import pe.medigest.farmacia.regla.PoliticaVigencia;
import pe.medigest.farmacia.regla.ResultadoValidacion;
import pe.medigest.farmacia.regla.VigenciaEstandar;
import pe.medigest.farmacia.regla.VigenciaExtendida;
import pe.medigest.farmacia.reporte.BuscadorTickets;
import pe.medigest.farmacia.reporte.EstadisticasFarmacia;
import pe.medigest.farmacia.reporte.ExportadorTickets;
import pe.medigest.farmacia.reporte.FiltroTickets;
import pe.medigest.farmacia.reporte.ReporteDispensaciones;
import pe.medigest.farmacia.servicio.ConsultaMedicamentos;
import pe.medigest.farmacia.servicio.ConstanciaCSV;
import pe.medigest.farmacia.servicio.ConstanciaTexto;
import pe.medigest.farmacia.servicio.DisponibilidadMedicamento;
import pe.medigest.farmacia.servicio.HistorialSesionDispensacion;
import pe.medigest.farmacia.servicio.NotificadorDispensacion;
import pe.medigest.farmacia.servicio.NumeradorTicket;
import pe.medigest.farmacia.servicio.ResultadoDispensacion;
import pe.medigest.farmacia.tiempo.RelojFijo;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.Producto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FarmaciaNuevasClasesTest {

    private final LocalDate hoy = LocalDate.of(2026, 11, 20);
    private final RelojFijo reloj = new RelojFijo(hoy);

    private Receta receta(String estado, int diasAtras, int cantidad) {
        Receta r = new Receta(1, "REC-X", estado, hoy.minusDays(diasAtras), "Paciente de prueba", "12345678");
        r.agregarDetalle(new pe.medigest.farmacia.DetalleReceta(1, "Paracetamol", cantidad));
        return r;
    }

    private TicketFarmacia ticket(int id, String receta, String paciente, String usuario,
                                  LocalDateTime fecha, String estado, int... cantidadesPorLote) {
        TicketFarmacia t = new TicketFarmacia(id, id, receta, paciente, fecha, 1, usuario, estado);
        int n = 1;
        for (int c : cantidadesPorLote) {
            t.agregarDetalle(new DetalleTicket(1, "Paracetamol", n, "LOTE-" + n, c));
            n++;
        }
        return t;
    }

    // ---------------------------------------------------------------- cadena de validación
    @Test
    void laCadenaPorDefectoAceptaUnaRecetaCorrecta() {
        CadenaValidacionReceta cadena = CadenaValidacionReceta.porDefecto(reloj);
        assertDoesNotThrow(() -> cadena.validarOLanzar(receta(Receta.EMITIDA, 5, 3)));
        assertTrue(cadena.evaluar(receta(Receta.EMITIDA, 5, 3)).esValida());
        assertEquals(5, cadena.cantidadReglas());
    }

    @Test
    void cadaProblemaLanzaSuExcepcionEspecifica() {
        CadenaValidacionReceta cadena = CadenaValidacionReceta.porDefecto(reloj);
        assertThrows(RecetaNoEncontradaException.class, () -> cadena.validarOLanzar(null));
        assertThrows(RecetaYaDispensadaException.class, () -> cadena.validarOLanzar(receta(Receta.DISPENSADA, 1, 3)));
        assertThrows(RecetaVencidaException.class, () -> cadena.validarOLanzar(receta(Receta.EMITIDA, 45, 3)));
        // todas son RecetaInvalidaException, como esperaba el código anterior
        assertThrows(RecetaInvalidaException.class, () -> cadena.validarOLanzar(receta(Receta.EMITIDA, 1, 0)));
    }

    @Test
    void evaluarJuntaTodosLosProblemas() {
        Receta mala = new Receta(2, "REC-M", Receta.DISPENSADA, hoy.minusDays(90), "Paciente", "1");
        ResultadoValidacion r = CadenaValidacionReceta.porDefecto(reloj).evaluar(mala);
        assertFalse(r.esValida());
        assertEquals(Arrays.asList(MotivoRechazo.YA_DISPENSADA, MotivoRechazo.RECETA_VENCIDA, MotivoRechazo.SIN_MEDICAMENTOS),
                r.getMotivos());
        assertEquals(1, CadenaValidacionReceta.porDefecto(reloj).evaluar(null).getMotivos().size());
        assertEquals("Receta válida.", CadenaValidacionReceta.porDefecto(reloj).evaluar(receta(Receta.EMITIDA, 0, 1)).resumen());
    }

    @Test
    void laVigenciaDependeDelRelojYDeLaPolitica() {
        Receta r = new Receta(1, "REC-1", Receta.EMITIDA, LocalDate.of(2026, 10, 21), "P", "1");
        PoliticaVigencia estandar = new VigenciaEstandar();
        assertTrue(estandar.estaVigente(r, LocalDate.of(2026, 11, 20)));   // día 30: todavía vale
        assertFalse(estandar.estaVigente(r, LocalDate.of(2026, 11, 21)));  // día 31: vencida
        assertTrue(new VigenciaExtendida().estaVigente(r, LocalDate.of(2026, 11, 21)));
        assertEquals(90, new VigenciaExtendida().diasDeVigencia(r));
        reloj.avanzarDias(10);
        assertEquals(LocalDate.of(2026, 11, 30), reloj.hoy());
    }

    @Test
    void laCadenaEstrictaRechazaMedicamentosRepetidos() {
        Receta r = receta(Receta.EMITIDA, 1, 2);
        r.agregarDetalle(new pe.medigest.farmacia.DetalleReceta(1, "Paracetamol", 4));
        assertDoesNotThrow(() -> CadenaValidacionReceta.porDefecto(reloj).validarOLanzar(r));
        assertThrows(RecetaInvalidaException.class, () -> CadenaValidacionReceta.estricta(reloj).validarOLanzar(r));
    }

    @Test
    void elValidadorOriginalSigueFuncionandoYAhoraPuedeEvaluar() {
        ValidadorReceta v = new ValidadorReceta();
        assertThrows(RecetaInvalidaException.class, () -> v.validar(null));
        assertTrue(v.evaluar(receta(Receta.EMITIDA, 0, 1)).esValida());
    }

    // ---------------------------------------------------------------- modelo
    @Test
    void documentosYCodigosSeValidan() {
        assertEquals("DNI 12345678", DocumentoPaciente.dni("12345678").toString());
        assertThrows(DocumentoInvalidoException.class, () -> DocumentoPaciente.dni("1234"));
        assertThrows(DocumentoInvalidoException.class, () -> DocumentoPaciente.de(null, "12345678"));
        assertTrue(TipoDocumento.CARNET_EXTRANJERIA.aceptaNumero("001234567"));
        assertEquals("REC-001", CodigoReceta.de(" rec-001 ").getValor());
        assertThrows(RecetaInvalidaException.class, () -> CodigoReceta.de(""));
        assertEquals(EstadoReceta.DISPENSADA, EstadoReceta.desde("dispensada"));
        assertEquals(EstadoTicket.ANULADO, EstadoTicket.desde("ANULADO"));
        assertThrows(IllegalArgumentException.class, () -> EstadoTicket.desde("???"));
    }

    @Test
    void elBuilderArmaRecetasValidas() {
        Receta r = new RecetaBuilder().conId(9).conCodigo("rec-9").emitidaEl(hoy.minusDays(2))
                .paraPaciente("Ana Torres", "12345678")
                .conMedicamento(1, "Paracetamol", 10).conMedicamento(2, "Ibuprofeno", 5).build();
        assertEquals("REC-9", r.getCodigo());
        assertEquals(2, r.getDetalles().size());
        assertEquals(Receta.EMITIDA, r.getEstado());
        assertTrue(new RecetaBuilder().conCodigo("rec-8").dispensada().build().estaDispensada());
        assertThrows(RecetaInvalidaException.class, () -> new RecetaBuilder().build());
    }

    @Test
    void elNumeradorDaFormatoAlTicket() {
        assertEquals("TK-000123", NumeradorTicket.formatear(123));
        assertEquals(123, NumeradorTicket.extraer("tk-000123"));
        assertThrows(ReglaNegocioException.class, () -> NumeradorTicket.formatear(0));
        assertThrows(ReglaNegocioException.class, () -> NumeradorTicket.extraer("XX-1"));
        assertThrows(ReglaNegocioException.class, () -> NumeradorTicket.extraer("TK-abc"));
    }

    // ---------------------------------------------------------------- constancias y resultado
    @Test
    void laConstanciaDelTicketUsaElFormateadorDeTexto() {
        TicketFarmacia t = ticket(5, "REC-001", "Ana Torres", "farmacia", LocalDateTime.of(2026, 11, 20, 9, 30),
                TicketFarmacia.DISPENSADO, 6, 4);
        String texto = t.generarConstancia();
        assertEquals(new ConstanciaTexto().formatear(t), texto);
        assertTrue(texto.contains("Ticket N°:    5"));
        assertTrue(texto.contains("20/11/2026 09:30"));
        assertTrue(texto.contains("Total de unidades: 10"));
    }

    @Test
    void laConstanciaCsvTieneUnaLineaPorLote() {
        TicketFarmacia t = ticket(5, "REC-001", "Torres, Ana", "farmacia", LocalDateTime.of(2026, 11, 20, 9, 30),
                TicketFarmacia.DISPENSADO, 6, 4);
        String[] lineas = new ConstanciaCSV().formatear(t).split("\n");
        assertEquals(3, lineas.length);
        assertEquals("5,REC-001,\"Torres, Ana\",Paracetamol,LOTE-1,6", lineas[1]);
    }

    @Test
    void elResultadoAgrupaLosLotesPorMedicamento() {
        TicketFarmacia t = ticket(1, "REC-006", "Paciente", "farmacia", LocalDateTime.now(),
                TicketFarmacia.DISPENSADO, 40, 20);
        t.agregarDetalle(new DetalleTicket(2, "Ibuprofeno", 9, "IBU-1", 5));
        ResultadoDispensacion r = ResultadoDispensacion.desde(t);
        assertEquals(2, r.getLineas().size());
        assertEquals(60, r.getLineas().get(0).totalUnidades());
        assertEquals(2, r.getLineas().get(0).getLotesUsados().size());
        assertEquals(65, r.totalUnidades());
        assertTrue(r.usoVariosLotes());
    }

    // ---------------------------------------------------------------- consulta de medicamentos
    @Test
    void laConsultaDeMedicamentosMuestraSoloLoDispensable() {
        Producto con = new Producto(1, "PARA-1", "Paracetamol", "Caja", 10);
        con.agregarLote(new LoteAlmacen(1, 1, "A", hoy.plusDays(90).plusYears(1), 50));
        Producto vencido = new Producto(2, "LORA-1", "Loratadina", "Caja", 10);
        vencido.agregarLote(new LoteAlmacen(2, 2, "V", LocalDate.now().minusDays(3), 30));
        ConsultaMedicamentos consulta = new ConsultaMedicamentos(() -> Arrays.asList(con, vencido));
        assertEquals(2, consulta.buscar(null).size());
        List<DisponibilidadMedicamento> disponibles = consulta.soloDisponibles(null);
        assertEquals(1, disponibles.size());
        assertEquals("Paracetamol", disponibles.get(0).getNombre());
        assertTrue(consulta.puedeCubrir(1, 50));
        assertFalse(consulta.puedeCubrir(1, 51));
        assertFalse(consulta.puedeCubrir(2, 1));  // solo hay lote vencido
        assertFalse(consulta.puedeCubrir(99, 1)); // no existe
    }

    // ---------------------------------------------------------------- reportes
    private List<TicketFarmacia> tickets() {
        return Arrays.asList(
                ticket(1, "REC-001", "Ana Torres", "farmacia", LocalDateTime.of(2026, 11, 18, 10, 0), TicketFarmacia.DISPENSADO, 15),
                ticket(2, "REC-002", "Luis Pérez", "admin", LocalDateTime.of(2026, 11, 18, 11, 0), TicketFarmacia.ANULADO, 8),
                ticket(3, "REC-003", "Ana Torres", "farmacia", LocalDateTime.of(2026, 11, 19, 9, 0), TicketFarmacia.DISPENSADO, 5, 5));
    }

    @Test
    void lasEstadisticasNoCuentanLosAnulados() {
        EstadisticasFarmacia e = new EstadisticasFarmacia(tickets());
        assertEquals(3, e.totalTickets());
        assertEquals(1, e.ticketsAnulados());
        assertEquals(25, e.unidadesDispensadas());
        assertEquals(25, e.masDispensados(5).get(0).getUnidades());
        assertEquals(2, e.resumenesPorDia().size());
        assertEquals(1, e.resumenesPorDia().get(0).getAnulados());
        assertEquals(15, e.resumenesPorDia().get(0).getUnidades());
        assertEquals(2, e.ticketsPorUsuario().get("farmacia").intValue());
    }

    @Test
    void elFiltroCombinaCriterios() {
        BuscadorTickets b = new BuscadorTickets();
        assertEquals(3, b.buscar(tickets(), new FiltroTickets()).size());
        assertEquals(2, b.buscar(tickets(), new FiltroTickets().conTexto("ana")).size());
        assertEquals(1, b.buscar(tickets(), new FiltroTickets().conEstado("ANULADO")).size());
        assertEquals(1, b.buscar(tickets(), new FiltroTickets().desde(LocalDate.of(2026, 11, 19))).size());
        assertEquals(2, b.buscar(tickets(), new FiltroTickets().hasta(LocalDate.of(2026, 11, 18))).size());
        assertEquals(1, b.buscar(tickets(), new FiltroTickets().deUsuario("ADMIN")).size());
        assertEquals(1, b.buscar(tickets(), new FiltroTickets().conTexto("rec-003").deUsuario("farmacia")).size());
    }

    @Test
    void elExportadorGeneraCsvConEncabezado() {
        String csv = new ExportadorTickets().generarTexto(tickets());
        String[] lineas = csv.split("\n");
        assertEquals(4, lineas.length);
        assertTrue(lineas[0].startsWith("Ticket,Fecha,Receta"));
        assertTrue(lineas[1].contains("REC-001"));
        assertEquals(2, new ReporteDispensaciones().masDispensados(tickets(), 5).cantidadFilas() + 1);
        assertEquals(2, new ReporteDispensaciones().resumenDiario(tickets()).cantidadFilas());
    }

    // ---------------------------------------------------------------- observadores y DAOs
    @Test
    void losObservadoresRecibenElAvisoAunqueUnoFalle() {
        NotificadorDispensacion n = new NotificadorDispensacion();
        HistorialSesionDispensacion historial = new HistorialSesionDispensacion(2);
        n.registrar(new pe.medigest.farmacia.servicio.ObservadorDispensacion() {
            @Override
            public void dispensado(TicketFarmacia ticket) {
                throw new RuntimeException("falla a propósito");
            }
        });
        n.registrar(historial);
        TicketFarmacia t = ticket(1, "REC-001", "Ana", "farmacia", LocalDateTime.now(), TicketFarmacia.DISPENSADO, 3);
        n.notificarDispensado(t);
        n.notificarAnulado(t);
        n.notificarDispensado(ticket(2, "REC-002", "Ana", "farmacia", LocalDateTime.now(), TicketFarmacia.DISPENSADO, 1));
        assertEquals(2, historial.getLineas().size());
        assertTrue(historial.getLineas().get(0).startsWith("Dispensado ticket 2"));
        assertEquals(2, n.cantidad());
    }

    @Test
    void losDaoEnMemoriaSiguenElMismoContratoQueLosJdbc() {
        RecetaDAOMemoria recetas = new RecetaDAOMemoria();
        Receta r = receta(Receta.EMITIDA, 1, 4);
        recetas.insertar(r);
        assertEquals(1, recetas.buscarPorCodigo("REC-X").getId());
        assertNull(recetas.buscarPorCodigo("NOPE"));
        recetas.actualizarEstado(r.getId(), Receta.DISPENSADA);
        assertTrue(recetas.buscarPorCodigo("REC-X").estaDispensada());
        recetas.actualizarEstado(r.getId(), Receta.EMITIDA);
        assertFalse(recetas.buscarPorCodigo("REC-X").estaDispensada());

        TicketDAOMemoria dao = new TicketDAOMemoria();
        TicketFarmacia t = ticket(0, "REC-X", "Ana Torres", "farmacia", LocalDateTime.now(), TicketFarmacia.DISPENSADO, 4);
        dao.insertar(t);
        assertEquals(1, dao.listarPorReceta("rec-x").size());
        assertEquals(1, dao.listarPorPaciente("torres").size());
        dao.actualizarEstado(t.getId(), TicketFarmacia.ANULADO);
        assertEquals(TicketFarmacia.ANULADO, dao.buscarPorId(t.getId()).getEstado());
        assertEquals(1, dao.listarUltimos(10).size());
    }
}
