package pe.medigest.inventario.memoria;

import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.LoteDAO;
import pe.medigest.inventario.regla.ComparadoresLote;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DAO que guarda los lotes en memoria. */
public class LoteDAOMemoria implements LoteDAO {

    private final Map<Integer, LoteAlmacen> datos = new LinkedHashMap<>();
    private int siguienteId = 1;

    @Override
    public synchronized void insertar(LoteAlmacen l) {
        l.setId(siguienteId++);
        datos.put(l.getId(), l);
    }

    @Override
    public synchronized void actualizar(LoteAlmacen l) {
        datos.put(l.getId(), l);
    }

    @Override
    public synchronized void eliminar(int id) {
        datos.remove(id);
    }

    @Override
    public synchronized LoteAlmacen buscarPorId(int id) {
        return datos.get(id);
    }

    @Override
    public synchronized List<LoteAlmacen> listar() {
        List<LoteAlmacen> lista = new ArrayList<>(datos.values());
        lista.sort(ComparadoresLote.porVencimiento());
        return lista;
    }

    @Override
    public synchronized List<LoteAlmacen> listarPorProducto(int productoId) {
        List<LoteAlmacen> lista = new ArrayList<>();
        for (LoteAlmacen l : datos.values()) {
            if (l.getProductoId() == productoId) {
                lista.add(l);
            }
        }
        lista.sort(ComparadoresLote.porIngreso());
        return lista;
    }

    @Override
    public synchronized LoteAlmacen buscarPorProductoYNroLote(int productoId, String nroLote) {
        for (LoteAlmacen l : datos.values()) {
            if (l.getProductoId() == productoId && l.getNroLote().equals(nroLote)) {
                return l;
            }
        }
        return null;
    }

    @Override
    public synchronized void actualizarCantidad(int loteId, int nuevaCantidad) {
        LoteAlmacen l = datos.get(loteId);
        if (l != null) {
            int actual = l.getCantidadDisponible();
            if (actual > 0) {
                l.descontar(actual);
            }
            if (nuevaCantidad > 0) {
                l.agregar(nuevaCantidad);
            }
        }
    }
}
