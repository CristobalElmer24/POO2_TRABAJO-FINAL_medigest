package pe.medigest.farmacia;

import pe.medigest.comun.AuditoriaLogger;
import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.comun.Usuario;
import pe.medigest.comun.Validaciones;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.Producto;
import pe.medigest.farmacia.servicio.NotificadorDispensacion;
import pe.medigest.farmacia.servicio.ObservadorDispensacion;
import pe.medigest.inventario.ServicioInventario;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Controlador MVC del módulo Farmacia.
 *
 * La dispensación es una sola transacción: validar receta, descontar stock (en los lotes que elija FEFO),
 * guardar el ticket y marcar la receta como dispensada. Si algo falla, no se guarda nada (RNF05).
 */
public class DispensacionController {

    private final ServicioInventario inventario;
    private final Usuario usuario;
    private final ConexionBD bd = ConexionBD.getInstancia();
    private final RecetaDAO recetaDAO = new RecetaDAOJdbc();
    private final TicketDAO ticketDAO = new TicketDAOJdbc();
    private final ValidadorReceta validador = new ValidadorReceta();
    private final NotificadorDispensacion notificador = new NotificadorDispensacion();

    public DispensacionController(ServicioInventario inventario, Usuario usuario) {
        this.inventario = inventario;
        this.usuario = usuario;
    }

    /** Observer: quien se registre se entera de cada dispensación o anulación ya confirmada. */
    public void registrarObservador(ObservadorDispensacion observador) {
        notificador.registrar(observador);
    }

    public void quitarObservador(ObservadorDispensacion observador) {
        notificador.quitar(observador);
    }

    public boolean puedeDispensar() {
        return usuario.tienePermiso(Usuario.DISPENSAR);
    }

    public boolean puedeAnular() {
        return usuario.tienePermiso(Usuario.ANULAR_TICKET);
    }

    // ------------------------------------------------------------ Consultas

    /** Busca la receta y comprueba que se pueda dispensar (si no, lanza el motivo). */
    public Receta buscarReceta(String codigo) {
        String cod = Validaciones.textoObligatorio(codigo, "Código de receta").toUpperCase();
        Receta receta = recetaDAO.buscarPorCodigo(cod);
        validador.validar(receta);
        return receta;
    }

    /** Medicamentos con su stock (RF09). */
    public List<Producto> consultarMedicamentos() {
        return inventario.listarProductos();
    }

    public int stockDisponible(int productoId) {
        return inventario.stockUtilizable(productoId);
    }

    public List<TicketFarmacia> historialPorReceta(String codigo) {
        return ticketDAO.listarPorReceta(Validaciones.textoObligatorio(codigo, "Código de receta").toUpperCase());
    }

    public List<TicketFarmacia> historialPorPaciente(String texto) {
        return ticketDAO.listarPorPaciente(Validaciones.textoObligatorio(texto, "Paciente o documento"));
    }

    public List<TicketFarmacia> ultimosTickets() {
        return ticketDAO.listarUltimos(50);
    }

    // ------------------------------------------------------------ Operaciones

    public TicketFarmacia dispensar(String codigoReceta) {
        String codigo = Validaciones.textoObligatorio(codigoReceta, "Código de receta").toUpperCase();

        TicketFarmacia ticket = auditado("DISPENSAR", "Receta " + codigo, Usuario.DISPENSAR, () ->
                bd.enTransaccion(() -> {
                    Receta receta = recetaDAO.buscarPorCodigo(codigo);
                    validador.validar(receta);

                    TicketFarmacia nuevo = new TicketFarmacia(receta.getId(), receta.getCodigo(),
                            receta.getPacienteNombre(), usuario.getId(), usuario.getNombreUsuario());

                    for (DetalleReceta detalle : receta.getDetalles()) {
                        Map<LoteAlmacen, Integer> usados = inventario.descontarStock(
                                detalle.getProductoId(), detalle.getCantidad(), "Dispensación receta " + codigo);
                        for (Map.Entry<LoteAlmacen, Integer> uso : usados.entrySet()) {
                            nuevo.agregarDetalle(new DetalleTicket(detalle.getProductoId(), detalle.getNombreProducto(),
                                    uso.getKey().getId(), uso.getKey().getNroLote(), uso.getValue()));
                        }
                    }

                    ticketDAO.insertar(nuevo);
                    recetaDAO.actualizarEstado(receta.getId(), Receta.DISPENSADA);
                    return nuevo;
                }));

        inventario.generarAlertas(); // después del commit, para que las alertas vean el stock nuevo
        notificador.notificarDispensado(ticket);
        return ticket;
    }

    /** Anula un ticket: devuelve las unidades a sus lotes y deja la receta otra vez disponible. */
    public TicketFarmacia anularTicket(int ticketId) {
        TicketFarmacia ticket = auditado("ANULAR_TICKET", "Ticket " + ticketId, Usuario.ANULAR_TICKET, () ->
                bd.enTransaccion(() -> {
                    TicketFarmacia t = ticketDAO.buscarPorId(ticketId);
                    if (t == null) {
                        throw new ReglaNegocioException("El ticket no existe.");
                    }
                    t.anular();
                    for (DetalleTicket d : t.getDetalles()) {
                        inventario.reponerStock(d.getLoteId(), d.getCantidad(), "Anulación ticket " + ticketId);
                    }
                    ticketDAO.actualizarEstado(ticketId, TicketFarmacia.ANULADO);
                    recetaDAO.actualizarEstado(t.getRecetaId(), Receta.EMITIDA);
                    return t;
                }));

        inventario.generarAlertas();
        notificador.notificarAnulado(ticket);
        return ticket;
    }

    /** Revisa el permiso, ejecuta y registra el resultado en la bitácora. */
    private <T> T auditado(String accion, String entidad, String permiso, Supplier<T> operacion) {
        if (!usuario.tienePermiso(permiso)) {
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "DENEGADO: sin permiso");
            throw new ReglaNegocioException("Su rol (" + usuario.getRol() + ") no tiene permiso para esta operación.");
        }
        try {
            T resultado = operacion.get();
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "EXITOSO");
            return resultado;
        } catch (RuntimeException e) {
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "FALLIDO: " + e.getMessage());
            throw e;
        }
    }
}
