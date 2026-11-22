package pe.medigest.web;

import pe.medigest.comun.AuditoriaLogger;
import pe.medigest.comun.LoginController;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.comun.Usuario;
import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.DispensacionController;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaDAOJdbc;
import pe.medigest.farmacia.TicketFarmacia;
import pe.medigest.farmacia.ValidadorReceta;
import pe.medigest.farmacia.regla.ResultadoValidacion;
import pe.medigest.inventario.AlertaInventario;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.MovimientoInventario;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.ServicioInventario;
import pe.medigest.inventario.regla.ClasificadorEstadoLote;
import pe.medigest.inventario.regla.ClasificadorNivelStock;
import pe.medigest.inventario.regla.ComparadoresLote;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de rutas de la API. Cada ruta llama a los MISMOS controladores que usaba la versión de escritorio,
 * así los permisos, la bitácora y las reglas de negocio no se duplican.
 */
public class RutasApi {

    /** Una ruta: recibe la sesión (null si no inició sesión) y los parámetros; devuelve algo que se convierte a JSON. */
    @FunctionalInterface
    interface Manejador {
        Object manejar(Sesion sesion, Parametros p) throws Exception;
    }

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ServicioInventario servicio;
    private final Sesiones sesiones;
    private final LoginController login = new LoginController();
    private final ClasificadorNivelStock nivelStock = new ClasificadorNivelStock();
    private final ClasificadorEstadoLote estadoLote = new ClasificadorEstadoLote(ServicioInventario.DIAS_ALERTA_VENCIMIENTO);
    private final Map<String, Manejador> rutas = new LinkedHashMap<>();
    private final Map<String, Boolean> publicas = new LinkedHashMap<>();

    public RutasApi(ServicioInventario servicio, Sesiones sesiones) {
        this.servicio = servicio;
        this.sesiones = sesiones;
        registrar();
    }

    public boolean existe(String metodo, String ruta) { return rutas.containsKey(metodo + " " + ruta); }

    public boolean esPublica(String metodo, String ruta) { return publicas.containsKey(metodo + " " + ruta); }

    public Manejador buscar(String metodo, String ruta) { return rutas.get(metodo + " " + ruta); }

    public Sesiones getSesiones() { return sesiones; }

    private void ruta(String metodo, String ruta, Manejador m) { rutas.put(metodo + " " + ruta, m); }

    private void rutaPublica(String metodo, String ruta, Manejador m) {
        ruta(metodo, ruta, m);
        publicas.put(metodo + " " + ruta, true);
    }

    private void registrar() {
        // ---- sesión
        rutaPublica("POST", "/api/login", (s, p) -> {
            Usuario u = login.iniciarSesion(p.texto("usuario"), p.texto("clave"));
            String token = sesiones.crear(new Sesion(u, servicio));
            Map<String, Object> r = datosSesion(u);
            r.put("token", token);
            return r;
        });
        rutaPublica("GET", "/api/sesion", (s, p) -> {
            if (s == null) {
                throw new ErrorApi(401, "No hay sesión iniciada.");
            }
            return datosSesion(s.getUsuario());
        });
        rutaPublica("POST", "/api/logout", (s, p) -> {
            if (s != null) {
                AuditoriaLogger.registrar(s.getUsuario().getNombreUsuario(), "LOGOUT", "Sesión", "EXITOSO");
                sesiones.cerrar(s);
            }
            return Json.obj("ok", true);
        });

        // ---- inventario
        ruta("GET", "/api/productos", (s, p) -> {
            List<Object> lista = new ArrayList<>();
            for (Producto pr : s.getInventario().listarProductos()) {
                lista.add(productoJson(pr));
            }
            return lista;
        });
        ruta("POST", "/api/productos", (s, p) -> {
            if (p.texto("id").isEmpty()) {
                s.getInventario().registrarProducto(p.texto("codigo"), p.texto("nombre"), p.texto("presentacion"), p.texto("stockMinimo"));
            } else {
                s.getInventario().actualizarProducto(p.entero("id"), p.texto("codigo"), p.texto("nombre"),
                        p.texto("presentacion"), p.texto("stockMinimo"));
            }
            return Json.obj("ok", true);
        });
        ruta("POST", "/api/productos/eliminar", (s, p) -> {
            s.getInventario().eliminarProducto(servicio.buscarProducto(p.entero("id")));
            return Json.obj("ok", true);
        });
        ruta("GET", "/api/lotes", (s, p) -> {
            List<LoteAlmacen> lotes = new ArrayList<>();
            Map<Integer, String> nombres = new LinkedHashMap<>();
            for (Producto pr : s.getInventario().listarProductos()) {
                nombres.put(pr.getId(), pr.getNombre());
                lotes.addAll(pr.getLotes());
            }
            lotes.sort(ComparadoresLote.porVencimiento());
            List<Object> lista = new ArrayList<>();
            for (LoteAlmacen l : lotes) {
                lista.add(Json.obj("id", l.getId(), "productoId", l.getProductoId(), "producto", nombres.get(l.getProductoId()),
                        "nroLote", l.getNroLote(), "vence", l.getFechaVencimiento(), "cantidad", l.getCantidadDisponible(),
                        "estado", estadoLote.clasificar(l).name()));
            }
            return lista;
        });
        ruta("POST", "/api/entradas", (s, p) -> {
            s.getInventario().registrarEntrada(servicio.buscarProducto(p.entero("productoId")),
                    p.texto("nroLote"), p.texto("vence"), p.texto("cantidad"));
            return Json.obj("ok", true);
        });
        ruta("POST", "/api/ajustes", (s, p) -> {
            s.getInventario().registrarAjuste(buscarLote(p.entero("loteId")), p.texto("diferencia"), p.texto("motivo"));
            return Json.obj("ok", true);
        });
        ruta("GET", "/api/movimientos", (s, p) -> {
            List<Object> lista = new ArrayList<>();
            for (MovimientoInventario m : s.getInventario().listarMovimientos()) {
                lista.add(Json.obj("id", m.getId(), "fecha", m.getFecha().format(FECHA_HORA), "tipo", m.getTipo(),
                        "lote", m.getDetalleLote() != null ? m.getDetalleLote() : String.valueOf(m.getLoteId()),
                        "cantidad", m.getCantidad(), "motivo", m.getMotivo()));
            }
            return lista;
        });
        ruta("GET", "/api/alertas", (s, p) -> {
            List<Object> lista = new ArrayList<>();
            for (AlertaInventario a : s.getInventario().listarAlertas()) {
                lista.add(Json.obj("tipo", a.getTipo().name(), "producto", a.getProducto(),
                        "lote", a.getNroLote() == null ? "" : a.getNroLote(), "mensaje", a.getMensaje()));
            }
            return lista;
        });

        // ---- farmacia
        ruta("GET", "/api/receta", (s, p) -> recetaJson(s.getFarmacia(), p.texto("codigo")));
        ruta("POST", "/api/dispensar", (s, p) -> ticketJson(s.getFarmacia().dispensar(p.texto("codigo"))));
        ruta("GET", "/api/tickets", (s, p) -> {
            DispensacionController f = s.getFarmacia();
            String texto = p.texto("texto");
            List<TicketFarmacia> tickets = texto.isEmpty() ? f.ultimosTickets()
                    : "paciente".equals(p.texto("criterio")) ? f.historialPorPaciente(texto) : f.historialPorReceta(texto);
            List<Object> lista = new ArrayList<>();
            for (TicketFarmacia t : tickets) {
                lista.add(ticketJson(t));
            }
            return lista;
        });
        ruta("POST", "/api/tickets/anular", (s, p) -> ticketJson(s.getFarmacia().anularTicket(p.entero("id"))));

        // ---- bitácora (solo quien tiene permiso)
        ruta("GET", "/api/bitacora", (s, p) -> {
            if (!s.getUsuario().tienePermiso(Usuario.VER_BITACORA)) {
                throw new ErrorApi(403, "Su rol (" + s.getUsuario().getRol() + ") no puede ver la bitácora.");
            }
            List<Object> lista = new ArrayList<>();
            for (String[] f : AuditoriaLogger.listarUltimos(200)) {
                lista.add(Json.obj("usuario", f[0], "accion", f[1], "entidad", f[2], "fecha", f[3], "resultado", f[4]));
            }
            return lista;
        });
    }

    // ------------------------------------------------------------ conversión a JSON

    private Map<String, Object> datosSesion(Usuario u) {
        return Json.obj("usuario", u.getNombreUsuario(), "rol", u.getRol(),
                "puedeDispensar", u.tienePermiso(Usuario.DISPENSAR),
                "puedeAnular", u.tienePermiso(Usuario.ANULAR_TICKET),
                "puedeGestionar", u.tienePermiso(Usuario.GESTIONAR_INVENTARIO),
                "puedeVerBitacora", u.tienePermiso(Usuario.VER_BITACORA));
    }

    private Map<String, Object> productoJson(Producto p) {
        int utilizable = p.stockUtilizable();
        return Json.obj("id", p.getId(), "codigo", p.getCodigo(), "nombre", p.getNombre(), "presentacion", p.getPresentacion(),
                "stockMinimo", p.getStockMinimo(), "stockTotal", p.stockTotal(), "stockUtilizable", utilizable,
                "nivel", nivelStock.clasificar(utilizable, p.getStockMinimo()).name());
    }

    private Map<String, Object> ticketJson(TicketFarmacia t) {
        List<Object> detalles = new ArrayList<>();
        t.getDetalles().forEach(d -> detalles.add(Json.obj("medicamento", d.getNombreProducto(),
                "lote", d.getNroLote(), "cantidad", d.getCantidad())));
        return Json.obj("id", t.getId(), "receta", t.getRecetaCodigo(), "paciente", t.getPacienteNombre(),
                "fecha", t.getFechaHora().format(FECHA_HORA), "usuario", t.getUsuarioNombre(), "estado", t.getEstado(),
                "unidades", t.totalUnidades(), "detalles", detalles, "constancia", t.generarConstancia());
    }

    /** Muestra la receta aunque no se pueda dispensar, con TODOS los motivos (así el usuario entiende qué pasa). */
    private Map<String, Object> recetaJson(DispensacionController farmacia, String codigo) {
        if (codigo.isEmpty()) {
            throw new ReglaNegocioException("Código de receta es obligatorio.");
        }
        Receta r = new RecetaDAOJdbc().buscarPorCodigo(codigo.toUpperCase());
        ResultadoValidacion v = new ValidadorReceta().evaluar(r);
        if (r == null) {
            return Json.obj("existe", false, "valida", false, "motivos", v.getMensajes());
        }
        List<Object> detalles = new ArrayList<>();
        for (DetalleReceta d : r.getDetalles()) {
            detalles.add(Json.obj("medicamento", d.getNombreProducto(), "cantidad", d.getCantidad(),
                    "disponible", farmacia.stockDisponible(d.getProductoId())));
        }
        return Json.obj("existe", true, "codigo", r.getCodigo(), "estado", r.getEstado(), "fecha", r.getFechaEmision(),
                "paciente", r.getPacienteNombre(), "documento", r.getPacienteDocumento(), "detalles", detalles,
                "valida", v.esValida(), "motivos", v.getMensajes());
    }

    private LoteAlmacen buscarLote(int loteId) {
        for (Producto p : servicio.listarProductos()) {
            for (LoteAlmacen l : p.getLotes()) {
                if (l.getId() == loteId) {
                    return l;
                }
            }
        }
        throw new ReglaNegocioException("El lote no existe.");
    }
}
