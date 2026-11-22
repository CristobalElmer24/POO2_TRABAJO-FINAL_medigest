package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

public class MovimientoEntrada extends MovimientoInventario {

    public MovimientoEntrada(int loteId, int cantidad, String motivo) {
        super(loteId, cantidad, ENTRADA, motivo);
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de una entrada debe ser mayor a 0.");
        }
    }

    @Override
    public void aplicar(LoteAlmacen lote) {
        lote.agregar(cantidad);
    }
}
