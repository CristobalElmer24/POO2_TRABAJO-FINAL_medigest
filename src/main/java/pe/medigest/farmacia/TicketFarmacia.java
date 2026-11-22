package pe.medigest.farmacia;

import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.farmacia.servicio.ConstanciaTexto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Constancia de una dispensación: quién atendió, cuándo, qué receta y qué lotes se entregaron. */
public class TicketFarmacia {

    public static final String DISPENSADO = "DISPENSADO";
    public static final String ANULADO = "ANULADO";

    private int id;
    private final int recetaId;
    private final String recetaCodigo;
    private final String pacienteNombre;
    private final LocalDateTime fechaHora;
    private final int usuarioId;
    private final String usuarioNombre;
    private String estado;
    private final List<DetalleTicket> detalles = new ArrayList<>();

    /** Ticket nuevo (fecha de ahora, estado DISPENSADO). */
    public TicketFarmacia(int recetaId, String recetaCodigo, String pacienteNombre, int usuarioId, String usuarioNombre) {
        this(0, recetaId, recetaCodigo, pacienteNombre, LocalDateTime.now(), usuarioId, usuarioNombre, DISPENSADO);
    }

    /** Ticket leído de la base de datos. */
    public TicketFarmacia(int id, int recetaId, String recetaCodigo, String pacienteNombre,
                          LocalDateTime fechaHora, int usuarioId, String usuarioNombre, String estado) {
        this.id = id;
        this.recetaId = recetaId;
        this.recetaCodigo = recetaCodigo;
        this.pacienteNombre = pacienteNombre;
        this.fechaHora = fechaHora;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.estado = estado;
    }

    public void agregarDetalle(DetalleTicket detalle) { detalles.add(detalle); }

    public void anular() {
        if (ANULADO.equals(estado)) {
            throw new ReglaNegocioException("El ticket " + id + " ya estaba anulado.");
        }
        estado = ANULADO;
    }

    public int totalUnidades() {
        int total = 0;
        for (DetalleTicket d : detalles) {
            total += d.getCantidad();
        }
        return total;
    }

    /** Texto listo para mostrar en pantalla como constancia de dispensación. */
    public String generarConstancia() {
        return new ConstanciaTexto().formatear(this);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getRecetaId() { return recetaId; }

    public String getRecetaCodigo() { return recetaCodigo; }

    public String getPacienteNombre() { return pacienteNombre; }

    public LocalDateTime getFechaHora() { return fechaHora; }

    public int getUsuarioId() { return usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }

    public String getEstado() { return estado; }

    public List<DetalleTicket> getDetalles() { return Collections.unmodifiableList(detalles); }
}
