package pe.medigest.farmacia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Receta emitida por el médico (la crea el módulo Atención; aquí se usan datos de prueba).
 * Es válida solo durante DIAS_VIGENCIA días desde que se emite.
 */
public class Receta {

    public static final String EMITIDA = "EMITIDA";
    public static final String DISPENSADA = "DISPENSADA";
    public static final int DIAS_VIGENCIA = 30;

    private int id;
    private final String codigo;
    private String estado;
    private final LocalDate fechaEmision;
    private final String pacienteNombre;
    private final String pacienteDocumento;
    private final List<DetalleReceta> detalles = new ArrayList<>();

    public Receta(int id, String codigo, String estado, LocalDate fechaEmision,
                  String pacienteNombre, String pacienteDocumento) {
        this.id = id;
        this.codigo = codigo;
        this.estado = estado;
        this.fechaEmision = fechaEmision;
        this.pacienteNombre = pacienteNombre;
        this.pacienteDocumento = pacienteDocumento;
    }

    public void agregarDetalle(DetalleReceta detalle) { detalles.add(detalle); }

    public boolean estaDispensada() { return DISPENSADA.equals(estado); }

    public boolean estaVigente() {
        return estaVigente(LocalDate.now());
    }

    public boolean estaVigente(LocalDate hoy) {
        return !fechaEmision.plusDays(DIAS_VIGENCIA).isBefore(hoy);
    }

    public void marcarDispensada() { this.estado = DISPENSADA; }

    public void marcarEmitida() { this.estado = EMITIDA; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }

    public String getEstado() { return estado; }

    public LocalDate getFechaEmision() { return fechaEmision; }

    public String getPacienteNombre() { return pacienteNombre; }

    public String getPacienteDocumento() { return pacienteDocumento; }

    public List<DetalleReceta> getDetalles() { return Collections.unmodifiableList(detalles); }
}
