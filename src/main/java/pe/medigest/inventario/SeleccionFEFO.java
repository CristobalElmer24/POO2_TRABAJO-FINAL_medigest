package pe.medigest.inventario;

import java.util.Comparator;

/** FEFO: primero en vencer, primero en salir. Es el criterio normal en farmacia. */
public class SeleccionFEFO extends SeleccionLoteBase {

    @Override
    protected Comparator<LoteAlmacen> criterio() {
        return Comparator.comparing(LoteAlmacen::getFechaVencimiento).thenComparingInt(LoteAlmacen::getId);
    }

    @Override
    public String getNombre() {
        return "FEFO (primero en vencer, primero en salir)";
    }
}
