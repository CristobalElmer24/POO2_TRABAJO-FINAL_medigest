package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

/**
 * PATRÓN: Factory Method (versión simple con método estático).
 * Centraliza la creación de movimientos: el resto del código no hace "new MovimientoSalida(...)" directamente.
 */
public class MovimientoFactory {

    private MovimientoFactory() { }

    public static MovimientoInventario crear(String tipo, int loteId, int cantidad, String motivo) {
        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de movimiento es obligatorio.");
        }
        switch (tipo.toUpperCase()) {
            case MovimientoInventario.ENTRADA:
                return new MovimientoEntrada(loteId, cantidad, motivo);
            case MovimientoInventario.SALIDA:
                return new MovimientoSalida(loteId, cantidad, motivo);
            case MovimientoInventario.AJUSTE:
                return new MovimientoAjuste(loteId, cantidad, motivo);
            default:
                throw new ReglaNegocioException("Tipo de movimiento no válido: " + tipo);
        }
    }
}
