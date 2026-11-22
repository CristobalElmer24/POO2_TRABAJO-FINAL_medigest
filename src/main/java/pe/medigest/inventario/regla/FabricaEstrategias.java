package pe.medigest.inventario.regla;

import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.EstrategiaSeleccionLote;
import pe.medigest.inventario.SeleccionFEFO;
import pe.medigest.inventario.SeleccionFIFO;

import java.util.Arrays;
import java.util.List;

/** Factory: devuelve la estrategia de selección de lotes a partir de su nombre corto (FEFO, FIFO, MENOR). */
public final class FabricaEstrategias {

    private FabricaEstrategias() { }

    public static EstrategiaSeleccionLote crear(String nombre) {
        if (nombre == null) {
            throw new ReglaNegocioException("El nombre de la estrategia es obligatorio.");
        }
        switch (nombre.trim().toUpperCase()) {
            case "FEFO":
                return new SeleccionFEFO();
            case "FIFO":
                return new SeleccionFIFO();
            case "MENOR":
                return new SeleccionMenorCantidad();
            default:
                throw new ReglaNegocioException("Estrategia desconocida: " + nombre + ". Use FEFO, FIFO o MENOR.");
        }
    }

    public static List<String> nombresDisponibles() {
        return Arrays.asList("FEFO", "FIFO", "MENOR");
    }
}
