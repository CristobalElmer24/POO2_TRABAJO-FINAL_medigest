package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

import java.time.LocalDate;

/** Lote cuya fecha de vencimiento ya pasó. */
public class LoteVencidoEspecificacion implements EspecificacionLote {
    private final LocalDate hoy;

    public LoteVencidoEspecificacion() {
        this(LocalDate.now());
    }

    public LoteVencidoEspecificacion(LocalDate hoy) {
        this.hoy = hoy;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return lote.estaVencido(hoy);
    }
}
