package pe.medigest.inventario.memoria;

import pe.medigest.inventario.MovimientoDAO;
import pe.medigest.inventario.MovimientoInventario;

import java.util.ArrayList;
import java.util.List;

/** DAO que guarda los movimientos en memoria. */
public class MovimientoDAOMemoria implements MovimientoDAO {

    private final List<MovimientoInventario> datos = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public synchronized void insertar(MovimientoInventario m) {
        m.setId(siguienteId++);
        datos.add(m);
    }

    @Override
    public synchronized List<MovimientoInventario> listarUltimos(int cantidad) {
        List<MovimientoInventario> resultado = new ArrayList<>();
        for (int i = datos.size() - 1; i >= 0 && resultado.size() < cantidad; i--) {
            resultado.add(datos.get(i));
        }
        return resultado;
    }
}
