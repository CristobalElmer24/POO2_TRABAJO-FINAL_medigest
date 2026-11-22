package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

import java.time.LocalDate;

/** Lote que todavía no venció (vence hoy o después). */
public class LoteVigente implements EspecificacionLote {
    private final LocalDate hoy;

    public LoteVigente() {
        this(LocalDate.now());
    }

    public LoteVigente(LocalDate hoy) {
        this.hoy = hoy;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return !lote.estaVencido(hoy);
    }
}
