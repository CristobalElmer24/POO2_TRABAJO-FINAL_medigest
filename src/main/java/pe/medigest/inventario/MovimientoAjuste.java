package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

/** Corrección manual del stock (merma, conteo físico, error de digitación...). La cantidad es la diferencia: +5 o -3. */
public class MovimientoAjuste extends MovimientoInventario {

    public MovimientoAjuste(int loteId, int diferencia, String motivo) {
        super(loteId, diferencia, AJUSTE, motivo);
        if (diferencia == 0) {
            throw new ReglaNegocioException("El ajuste no puede ser 0.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ReglaNegocioException("Todo ajuste necesita un motivo.");
        }
    }

    @Override
    public void aplicar(LoteAlmacen lote) {
        if (cantidad > 0) {
            lote.agregar(cantidad);
        } else {
            lote.descontar(-cantidad);
        }
    }
}
