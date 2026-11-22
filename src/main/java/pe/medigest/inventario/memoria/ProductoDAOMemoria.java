package pe.medigest.inventario.memoria;

import pe.medigest.inventario.Producto;
import pe.medigest.inventario.ProductoDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DAO que guarda los productos en memoria. Sirve para probar la lógica sin base de datos. */
public class ProductoDAOMemoria implements ProductoDAO {

    private final Map<Integer, Producto> datos = new LinkedHashMap<>();
    private int siguienteId = 1;

    @Override
    public synchronized void insertar(Producto p) {
        p.setId(siguienteId++);
        datos.put(p.getId(), p);
    }

    @Override
    public synchronized void actualizar(Producto p) {
        datos.put(p.getId(), p);
    }

    @Override
    public synchronized void eliminar(int id) {
        datos.remove(id);
    }

    @Override
    public synchronized Producto buscarPorId(int id) {
        return datos.get(id);
    }

    @Override
    public synchronized Producto buscarPorCodigo(String codigo) {
        for (Producto p : datos.values()) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Producto> listar() {
        List<Producto> lista = new ArrayList<>(datos.values());
        lista.sort(Comparator.comparing(Producto::getNombre));
        return lista;
    }
}
