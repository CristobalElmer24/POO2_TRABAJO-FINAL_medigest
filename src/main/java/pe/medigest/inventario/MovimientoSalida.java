package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

public class MovimientoSalida extends MovimientoInventario {

    public MovimientoSalida(int loteId, int cantidad, String motivo) {
        super(loteId, cantidad, SALIDA, motivo);
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de una salida debe ser mayor a 0.");
        }
    }

    @Override
    public void aplicar(LoteAlmacen lote) {
        lote.descontar(cantidad);
    }
}
