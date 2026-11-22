package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;

import java.time.LocalDate;

/** Lote vigente que vence dentro de los próximos N días. */
public class LotePorVencer implements EspecificacionLote {
    private final int dias;
    private final LocalDate hoy;

    public LotePorVencer(int dias) {
        this(dias, LocalDate.now());
    }

    public LotePorVencer(int dias, LocalDate hoy) {
        this.dias = dias;
        this.hoy = hoy;
    }

    @Override
    public boolean cumple(LoteAlmacen lote) {
        return !lote.estaVencido(hoy) && !lote.getFechaVencimiento().isAfter(hoy.plusDays(dias));
    }
}
