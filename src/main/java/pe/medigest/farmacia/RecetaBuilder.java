package pe.medigest.farmacia;

import pe.medigest.farmacia.modelo.CodigoReceta;
import pe.medigest.farmacia.modelo.DatosPaciente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** PATRÓN: Builder. Arma una Receta con sus medicamentos paso a paso. */
public class RecetaBuilder {

    private int id;
    private String codigo;
    private String estado = Receta.EMITIDA;
    private LocalDate fechaEmision = LocalDate.now();
    private String pacienteNombre;
    private String pacienteDocumento;
    private final List<DetalleReceta> detalles = new ArrayList<>();

    public RecetaBuilder conId(int id) {
        this.id = id;
        return this;
    }

    public RecetaBuilder conCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    public RecetaBuilder emitidaEl(LocalDate fecha) {
        this.fechaEmision = fecha;
        return this;
    }

    public RecetaBuilder dispensada() {
        this.estado = Receta.DISPENSADA;
        return this;
    }

    public RecetaBuilder paraPaciente(String nombre, String documento) {
        this.pacienteNombre = nombre;
        this.pacienteDocumento = documento;
        return this;
    }

    public RecetaBuilder paraPaciente(DatosPaciente datos) {
        return paraPaciente(datos.getNombre(), datos.getDocumento().getNumero());
    }

    public RecetaBuilder conMedicamento(int productoId, String nombre, int cantidad) {
        detalles.add(new DetalleReceta(productoId, nombre, cantidad));
        return this;
    }

    public Receta build() {
        Receta r = new Receta(id, CodigoReceta.de(codigo).getValor(), estado, fechaEmision,
                pacienteNombre, pacienteDocumento);
        for (DetalleReceta d : detalles) {
            r.agregarDetalle(d);
        }
        return r;
    }
}
