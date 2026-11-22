package pe.medigest.inventario.regla;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.excepcion.AjusteInvalidoException;

/** Reglas de un ajuste manual: diferencia distinta de 0, con motivo, y que no deje el lote en negativo. */
public class ValidadorAjuste {

    public void validar(LoteAlmacen lote, int diferencia, String motivo) {
        if (diferencia == 0) {
            throw new AjusteInvalidoException("El ajuste no puede ser 0.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new AjusteInvalidoException("Todo ajuste necesita un motivo.");
        }
        if (lote.getCantidadDisponible() + diferencia < 0) {
            throw new AjusteInvalidoException("El ajuste dejaría el lote " + lote.getNroLote() + " en negativo ("
                    + lote.getCantidadDisponible() + " " + diferencia + ").");
        }
    }
}
