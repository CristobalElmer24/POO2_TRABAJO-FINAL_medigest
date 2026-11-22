package pe.medigest.inventario.regla;

import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.LoteVencidoException;
import pe.medigest.inventario.modelo.NumeroLote;

import java.time.LocalDate;

/** Reglas de un lote nuevo: número válido, cantidad positiva y que no esté vencido. */
public class ValidadorLote {

    public void validarIngreso(String nroLote, LocalDate fechaVencimiento, int cantidad, LocalDate hoy) {
        NumeroLote.de(nroLote);
        if (fechaVencimiento == null) {
            throw new ReglaNegocioException("La fecha de vencimiento es obligatoria.");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de la entrada debe ser mayor a 0.");
        }
        if (fechaVencimiento.isBefore(hoy)) {
            throw new LoteVencidoException("No se puede ingresar un lote que ya está vencido.");
        }
    }

    public void validarIngreso(String nroLote, LocalDate fechaVencimiento, int cantidad) {
        validarIngreso(nroLote, fechaVencimiento, cantidad, LocalDate.now());
    }

    public void validar(LoteAlmacen lote) {
        validarIngreso(lote.getNroLote(), lote.getFechaVencimiento(), Math.max(lote.getCantidadDisponible(), 1),
                lote.getFechaVencimiento());
    }
}
