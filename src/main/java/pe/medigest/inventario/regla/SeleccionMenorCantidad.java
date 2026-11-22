package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.SeleccionLoteBase;

import java.util.Comparator;

/** Strategy: vacía primero los lotes con menos unidades, para no dejar lotes sueltos con 2 o 3 pastillas. */
public class SeleccionMenorCantidad extends SeleccionLoteBase {

    @Override
    protected Comparator<LoteAlmacen> criterio() {
        return ComparadoresLote.porCantidadAscendente();
    }

    @Override
    public String getNombre() {
        return "Menor cantidad primero";
    }
}
