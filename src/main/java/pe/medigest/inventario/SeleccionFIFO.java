package pe.medigest.inventario;

import java.util.Comparator;

/** FIFO: primero en entrar, primero en salir (se usa el lote que se registró antes). */
public class SeleccionFIFO extends SeleccionLoteBase {

    @Override
    protected Comparator<LoteAlmacen> criterio() {
        return Comparator.comparingInt(LoteAlmacen::getId);
    }

    @Override
    public String getNombre() {
        return "FIFO (primero en entrar, primero en salir)";
    }
}
