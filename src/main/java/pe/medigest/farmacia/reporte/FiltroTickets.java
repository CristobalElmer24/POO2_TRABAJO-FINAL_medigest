package pe.medigest.farmacia.reporte;

import pe.medigest.farmacia.TicketFarmacia;

import java.time.LocalDate;

/** Criterios para filtrar tickets: estado, rango de fechas, texto del paciente o de la receta y usuario. Los vacíos no filtran. */
public class FiltroTickets {
    private String estado;
    private LocalDate desde;
    private LocalDate hasta;
    private String texto;
    private String usuario;

    public FiltroTickets conEstado(String estado) { this.estado = estado; return this; }

    public FiltroTickets desde(LocalDate desde) { this.desde = desde; return this; }

    public FiltroTickets hasta(LocalDate hasta) { this.hasta = hasta; return this; }

    public FiltroTickets conTexto(String texto) { this.texto = texto; return this; }

    public FiltroTickets deUsuario(String usuario) { this.usuario = usuario; return this; }

    public boolean cumple(TicketFarmacia t) {
        if (estado != null && !estado.equalsIgnoreCase(t.getEstado())) {
            return false;
        }
        LocalDate dia = t.getFechaHora().toLocalDate();
        if (desde != null && dia.isBefore(desde)) {
            return false;
        }
        if (hasta != null && dia.isAfter(hasta)) {
            return false;
        }
        if (usuario != null && !usuario.equalsIgnoreCase(t.getUsuarioNombre())) {
            return false;
        }
        if (texto != null && !texto.trim().isEmpty()) {
            String buscado = texto.trim().toLowerCase();
            return t.getPacienteNombre().toLowerCase().contains(buscado)
                    || t.getRecetaCodigo().toLowerCase().contains(buscado);
        }
        return true;
    }
}
