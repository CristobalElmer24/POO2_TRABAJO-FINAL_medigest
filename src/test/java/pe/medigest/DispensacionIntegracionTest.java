package pe.medigest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.LoginController;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.comun.Usuario;
import pe.medigest.farmacia.DispensacionController;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.TicketFarmacia;
import pe.medigest.inventario.AlertaInventario;
import pe.medigest.inventario.InventarioController;
import pe.medigest.inventario.LoteVencidoException;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.ServicioInventario;
import pe.medigest.inventario.StockInsuficienteException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de punta a punta contra SQLite en memoria, usando los datos de prueba de InicializadorBD.
 * Cada prueba usa su propia receta y compara el stock "antes" y "después", así no dependen del orden.
 */
class DispensacionIntegracionTest {

    private static ServicioInventario inventario;
    private static Usuario farmacia;
    private static Usuario almacenero;
    private static DispensacionController dispensacion;

    @BeforeAll
    static void preparar() {
        ConexionBD.configurarUrl("jdbc:sqlite::memory:");
        InicializadorBD.preparar();
        inventario = new ServicioInventario();
        LoginController login = new LoginController();
        farmacia = login.iniciarSesion("farmacia", "farmacia");
        almacenero = login.iniciarSesion("inventario", "inventario");
        dispensacion = new DispensacionController(inventario, farmacia);
    }

    private static int stock(String codigoProducto) {
        for (Producto p : inventario.listarProductos()) {
            if (p.getCodigo().equals(codigoProducto)) {
                return p.stockUtilizable();
            }
        }
        throw new IllegalStateException("No existe el producto " + codigoProducto);
    }

    @Test
    void dispensarDescuentaElStockYSePuedeAnular() {
        int paraAntes = stock("P001");
        int ibuAntes = stock("P002");

        TicketFarmacia ticket = dispensacion.dispensar("REC-001");
        assertEquals(15, ticket.totalUnidades());
        assertEquals(paraAntes - 10, stock("P001"));
        assertEquals(ibuAntes - 5, stock("P002"));
        assertThrows(RecetaInvalidaException.class, () -> dispensacion.buscarReceta("REC-001")); // ya dispensada

        dispensacion.anularTicket(ticket.getId());
        assertEquals(paraAntes, stock("P001"));
        assertEquals(ibuAntes, stock("P002"));
        dispensacion.buscarReceta("REC-001"); // vuelve a estar disponible
    }

    @Test
    void siFallaUnMedicamentoNoSeDescuentaNada() {
        int paraAntes = stock("P001");
        int amoxAntes = stock("P003");

        // el paracetamol alcanza, la amoxicilina (40 pedidas, 25 en stock) no
        assertThrows(StockInsuficienteException.class, () -> dispensacion.dispensar("REC-004"));

        assertEquals(paraAntes, stock("P001"));
        assertEquals(amoxAntes, stock("P003"));
        dispensacion.buscarReceta("REC-004"); // la receta sigue sin dispensar
    }

    @Test
    void noSeDispensaConLotesVencidos() {
        assertThrows(LoteVencidoException.class, () -> dispensacion.dispensar("REC-005"));
    }

    @Test
    void noSeDispensaUnaRecetaYaDispensadaNiVencida() {
        assertThrows(RecetaInvalidaException.class, () -> dispensacion.dispensar("REC-002"));
        assertThrows(RecetaInvalidaException.class, () -> dispensacion.dispensar("REC-003"));
        assertThrows(RecetaInvalidaException.class, () -> dispensacion.dispensar("REC-999"));
    }

    @Test
    void fefoEmpiezaPorElLoteQueVenceAntes() {
        int antes = stock("P001");
        TicketFarmacia ticket = dispensacion.dispensar("REC-006");
        assertEquals(60, ticket.totalUnidades());
        assertEquals("PARA-C", ticket.getDetalles().get(0).getNroLote());
        assertTrue(ticket.getDetalles().size() >= 2);
        assertEquals(antes - 60, stock("P001"));
    }

    @Test
    void soloElRolCorrectoPuedeDispensarOModificarInventario() {
        DispensacionController comoAlmacenero = new DispensacionController(inventario, almacenero);
        assertThrows(ReglaNegocioException.class, () -> comoAlmacenero.dispensar("REC-001"));

        InventarioController comoFarmacia = new InventarioController(inventario, farmacia);
        assertThrows(ReglaNegocioException.class,
                () -> comoFarmacia.registrarProducto("P900", "Prueba", "1 mg", "5"));
    }

    @Test
    void entradasAjustesYCatalogoRespetanLasReglas() {
        InventarioController ctrl = new InventarioController(inventario, almacenero);
        Producto ibuprofeno = null;
        for (Producto p : ctrl.listarProductos()) {
            if (p.getCodigo().equals("P002")) {
                ibuprofeno = p;
            }
        }
        int antes = stock("P002");
        String venceEn = LocalDate.now().plusYears(1).toString();

        ctrl.registrarEntrada(ibuprofeno, "IBU-NUEVO", venceEn, "7");
        assertEquals(antes + 7, stock("P002"));

        final Producto ibu = ibuprofeno;
        assertThrows(LoteVencidoException.class, () ->
                ctrl.registrarEntrada(ibu, "IBU-VIEJO", LocalDate.now().minusDays(1).toString(), "5"));

        ctrl.registrarProducto("P777", "Producto temporal", "1 mg", "3");
        assertThrows(ReglaNegocioException.class, () -> ctrl.registrarProducto("P777", "Otro", "1 mg", "3"));
        assertThrows(ReglaNegocioException.class, () -> ctrl.registrarEntrada(ibu, "X", venceEn, "abc"));
        assertThrows(ReglaNegocioException.class, () -> ctrl.eliminarProducto(ibu)); // tiene lotes
    }

    @Test
    void lasAlertasSeCalculanYElObserverNoRepiteAvisos() {
        List<String> claves = new ArrayList<>();
        for (AlertaInventario a : inventario.calcularAlertas()) {
            claves.add(a.getTipo() + ":" + a.getProducto());
        }
        assertTrue(claves.contains("STOCK_BAJO:Amoxicilina"));
        assertTrue(claves.contains("STOCK_BAJO:Loratadina"));
        assertTrue(claves.contains("VENCIDO:Paracetamol"));

        ServicioInventario nuevo = new ServicioInventario();
        List<AlertaInventario> recibidas = new ArrayList<>();
        nuevo.registrarObservador(recibidas::add);

        nuevo.generarAlertas();
        int primeraVez = recibidas.size();
        assertTrue(primeraVez > 0);

        nuevo.generarAlertas(); // sin cambios en el stock: no debe avisar de nuevo
        assertEquals(primeraVez, recibidas.size());
        assertFalse(nuevo.getAlertasActuales().isEmpty());
    }
}
