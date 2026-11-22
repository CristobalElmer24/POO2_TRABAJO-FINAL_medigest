package pe.medigest.inventario;

import java.util.List;

public interface MovimientoDAO {
    void insertar(MovimientoInventario movimiento);

    /** Los últimos movimientos, el más reciente primero. */
    List<MovimientoInventario> listarUltimos(int cantidad);
}
