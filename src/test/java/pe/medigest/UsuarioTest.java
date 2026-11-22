package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.comun.Usuario;
import pe.medigest.comun.Validaciones;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    private final Usuario admin = new Usuario(1, "admin", Usuario.ADMINISTRADOR);
    private final Usuario farmacia = new Usuario(2, "farmacia", Usuario.FARMACIA);
    private final Usuario inventario = new Usuario(3, "inventario", Usuario.INVENTARIO);

    @Test
    void elAdministradorPuedeTodo() {
        assertTrue(admin.tienePermiso(Usuario.DISPENSAR));
        assertTrue(admin.tienePermiso(Usuario.GESTIONAR_INVENTARIO));
        assertTrue(admin.tienePermiso(Usuario.VER_BITACORA));
    }

    @Test
    void farmaciaSoloDispensaYAnula() {
        assertTrue(farmacia.tienePermiso(Usuario.DISPENSAR));
        assertTrue(farmacia.tienePermiso(Usuario.ANULAR_TICKET));
        assertFalse(farmacia.tienePermiso(Usuario.GESTIONAR_INVENTARIO));
        assertFalse(farmacia.tienePermiso(Usuario.VER_BITACORA));
    }

    @Test
    void inventarioSoloGestionaElInventario() {
        assertTrue(inventario.tienePermiso(Usuario.GESTIONAR_INVENTARIO));
        assertFalse(inventario.tienePermiso(Usuario.DISPENSAR));
    }

    @Test
    void elHashEsSha256Hexadecimal() {
        assertEquals(64, Usuario.generarHash("admin").length());
        assertEquals(Usuario.generarHash("admin"), Usuario.generarHash("admin"));
        assertFalse(Usuario.generarHash("admin").equals(Usuario.generarHash("Admin")));
    }

    @Test
    void lasValidacionesDeFormularioDanMensajesClaros() {
        assertThrows(ReglaNegocioException.class, () -> Validaciones.entero("abc", "Cantidad"));
        assertThrows(ReglaNegocioException.class, () -> Validaciones.enteroPositivo("0", "Cantidad"));
        assertThrows(ReglaNegocioException.class, () -> Validaciones.fecha("31/05/2027", "Vence"));
        assertEquals(2027, Validaciones.fecha("2027-05-31", "Vence").getYear());
    }
}
