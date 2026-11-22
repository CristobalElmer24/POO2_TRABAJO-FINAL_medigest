package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

public class StockInsuficienteException extends ReglaNegocioException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
