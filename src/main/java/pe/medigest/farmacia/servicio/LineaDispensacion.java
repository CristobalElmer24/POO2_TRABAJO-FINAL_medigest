package pe.medigest.farmacia.servicio;

import pe.medigest.farmacia.DetalleTicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Un medicamento entregado, con los lotes de los que salió (puede ser más de uno). */
public class LineaDispensacion {
    private final int productoId;
    private final String medicamento;
    private final List<DetalleTicket> lotesUsados = new ArrayList<>();

    public LineaDispensacion(int productoId, String medicamento) {
        this.productoId = productoId;
        this.medicamento = medicamento;
    }

    void agregar(DetalleTicket detalle) {
        lotesUsados.add(detalle);
    }

    public int getProductoId() { return productoId; }

    public String getMedicamento() { return medicamento; }

    public List<DetalleTicket> getLotesUsados() { return Collections.unmodifiableList(lotesUsados); }

    public int totalUnidades() {
        int total = 0;
        for (DetalleTicket d : lotesUsados) {
            total += d.getCantidad();
        }
        return total;
    }
}
