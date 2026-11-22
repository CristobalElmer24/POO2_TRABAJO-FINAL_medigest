package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.comun.Usuario;
import pe.medigest.inventario.ServicioInventario;
import pe.medigest.web.Json;
import pe.medigest.web.Sesion;
import pe.medigest.web.Sesiones;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebJsonTest {

    @Test
    void conviertePrimitivosListasYObjetos() {
        assertEquals("{\"id\":1,\"ok\":true,\"x\":null,\"lista\":[1,\"a\"]}",
                Json.aTexto(Json.obj("id", 1, "ok", true, "x", null, "lista", Arrays.asList(1, "a"))));
    }

    @Test
    void escapaComillasSaltosYAcentosSeQuedanIgual() {
        assertEquals("\"dijo \\\"hola\\\"\\nCLÍNICA\"", Json.aTexto("dijo \"hola\"\nCLÍNICA"));
        assertEquals("\"a\\\\b\\u0001\"", Json.aTexto("a\\b\u0001"));
    }

    @Test
    void lasSesionesSeCreanYSeCierran() {
        Sesiones sesiones = new Sesiones();
        Sesion s = new Sesion(new Usuario(1, "admin", Usuario.ADMINISTRADOR), new ServicioInventario());
        String token = sesiones.crear(s);
        assertTrue(token.length() >= 30);
        assertNotNull(sesiones.buscar(token));
        assertNull(sesiones.buscar("otro"));
        assertNull(sesiones.buscar(null));
        sesiones.cerrar(s);
        assertFalse(sesiones.cantidad() > 0);
    }
}
