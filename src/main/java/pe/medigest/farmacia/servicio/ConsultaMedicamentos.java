package pe.medigest.farmacia.servicio;

import pe.medigest.inventario.BuscadorProductos;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.regla.ClasificadorNivelStock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Consulta de medicamentos para el mostrador (RF09). Recibe de dónde sacar los productos, así se puede probar sin BD. */
public class ConsultaMedicamentos {

    private final Supplier<List<Producto>> fuente;
    private final BuscadorProductos buscador = new BuscadorProductos();
    private final ClasificadorNivelStock clasificador = new ClasificadorNivelStock();

    public ConsultaMedicamentos(Supplier<List<Producto>> fuente) {
        this.fuente = fuente;
    }

    public List<DisponibilidadMedicamento> buscar(String texto) {
        List<DisponibilidadMedicamento> lista = new ArrayList<>();
        for (Producto p : buscador.buscar(fuente.get(), texto)) {
            lista.add(new DisponibilidadMedicamento(p, clasificador));
        }
        return lista;
    }

    public List<DisponibilidadMedicamento> soloDisponibles(String texto) {
        List<DisponibilidadMedicamento> lista = new ArrayList<>();
        for (DisponibilidadMedicamento d : buscar(texto)) {
            if (d.estaDisponible()) {
                lista.add(d);
            }
        }
        return lista;
    }

    /** True si el medicamento existe y hay unidades dispensables suficientes. */
    public boolean puedeCubrir(int productoId, int cantidad) {
        for (DisponibilidadMedicamento d : buscar(null)) {
            if (d.getProductoId() == productoId) {
                return d.alcanzaPara(cantidad);
            }
        }
        return false;
    }
}
