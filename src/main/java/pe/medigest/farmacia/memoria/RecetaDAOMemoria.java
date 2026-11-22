package pe.medigest.farmacia.memoria;

import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaDAO;

import java.util.LinkedHashMap;
import java.util.Map;

/** DAO de recetas en memoria, para probar la lógica de farmacia sin base de datos. */
public class RecetaDAOMemoria implements RecetaDAO {

    private final Map<String, Receta> porCodigo = new LinkedHashMap<>();
    private int siguienteId = 1;

    @Override
    public synchronized Receta buscarPorCodigo(String codigo) {
        return porCodigo.get(codigo);
    }

    @Override
    public synchronized void insertar(Receta r) {
        r.setId(siguienteId++);
        for (DetalleReceta d : r.getDetalles()) {
            d.validarCantidad();
        }
        porCodigo.put(r.getCodigo(), r);
    }

    @Override
    public synchronized void actualizarEstado(int recetaId, String estado) {
        for (Receta r : porCodigo.values()) {
            if (r.getId() == recetaId) {
                if (Receta.DISPENSADA.equals(estado)) {
                    r.marcarDispensada();
                } else {
                    r.marcarEmitida();
                }
            }
        }
    }
}
