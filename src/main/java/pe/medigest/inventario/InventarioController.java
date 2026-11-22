package pe.medigest.inventario;

import pe.medigest.comun.AuditoriaLogger;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.comun.Usuario;
import pe.medigest.comun.Validaciones;

import java.util.List;
import java.util.function.Supplier;

/**
 * Controlador MVC del módulo Inventario. Recibe los textos de los formularios, los valida,
 * revisa el rol del usuario, llama al servicio y deja todo registrado en la bitácora.
 */
public class InventarioController {

    private final ServicioInventario servicio;
    private final Usuario usuario;

    public InventarioController(ServicioInventario servicio, Usuario usuario) {
        this.servicio = servicio;
        this.usuario = usuario;
    }

    public boolean puedeGestionar() {
        return usuario.tienePermiso(Usuario.GESTIONAR_INVENTARIO);
    }

    // ------------------------------------------------------------ Consultas (libres para todos los roles)

    public List<Producto> listarProductos() {
        return servicio.listarProductos();
    }

    public List<MovimientoInventario> listarMovimientos() {
        return servicio.listarMovimientos(100);
    }

    public List<AlertaInventario> listarAlertas() {
        servicio.generarAlertas();
        return servicio.getAlertasActuales();
    }

    // ------------------------------------------------------------ Operaciones que modifican datos

    public void registrarProducto(String codigo, String nombre, String presentacion, String stockMinimo) {
        String cod = Validaciones.textoObligatorio(codigo, "Código").toUpperCase();
        ejecutar("REGISTRAR_PRODUCTO", "Producto " + cod, () -> {
            servicio.registrarProducto(new Producto(cod,
                    Validaciones.textoObligatorio(nombre, "Nombre"),
                    Validaciones.textoObligatorio(presentacion, "Presentación"),
                    Validaciones.entero(stockMinimo, "Stock mínimo")));
            return null;
        });
    }

    public void actualizarProducto(int id, String codigo, String nombre, String presentacion, String stockMinimo) {
        String cod = Validaciones.textoObligatorio(codigo, "Código").toUpperCase();
        ejecutar("ACTUALIZAR_PRODUCTO", "Producto " + cod, () -> {
            servicio.actualizarProducto(new Producto(id, cod,
                    Validaciones.textoObligatorio(nombre, "Nombre"),
                    Validaciones.textoObligatorio(presentacion, "Presentación"),
                    Validaciones.entero(stockMinimo, "Stock mínimo")));
            return null;
        });
    }

    public void eliminarProducto(Producto producto) {
        ejecutar("ELIMINAR_PRODUCTO", "Producto " + producto.getCodigo(), () -> {
            servicio.eliminarProducto(producto.getId());
            return null;
        });
    }

    public void registrarEntrada(Producto producto, String nroLote, String fechaVencimiento, String cantidad) {
        ejecutar("ENTRADA_STOCK", "Producto " + producto.getCodigo() + " lote " + nroLote, () -> {
            servicio.registrarEntrada(producto.getId(),
                    Validaciones.textoObligatorio(nroLote, "Nro. de lote"),
                    Validaciones.fecha(fechaVencimiento, "Fecha de vencimiento"),
                    Validaciones.enteroPositivo(cantidad, "Cantidad"),
                    "Ingreso de mercadería");
            return null;
        });
    }

    public void registrarAjuste(LoteAlmacen lote, String diferencia, String motivo) {
        ejecutar("AJUSTE_STOCK", "Lote " + lote.getNroLote(), () -> {
            servicio.registrarAjuste(lote.getId(),
                    Validaciones.entero(diferencia, "Diferencia"),
                    Validaciones.textoObligatorio(motivo, "Motivo"));
            return null;
        });
    }

    /** Revisa el permiso, ejecuta la operación y deja constancia (exitosa o fallida) en la bitácora. */
    private <T> T ejecutar(String accion, String entidad, Supplier<T> operacion) {
        if (!puedeGestionar()) {
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "DENEGADO: sin permiso");
            throw new ReglaNegocioException("Su rol (" + usuario.getRol() + ") no puede modificar el inventario.");
        }
        try {
            T resultado = operacion.get();
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "EXITOSO");
            servicio.generarAlertas(); // las alertas se actualizan apenas cambia el stock
            return resultado;
        } catch (RuntimeException e) {
            AuditoriaLogger.registrar(usuario.getNombreUsuario(), accion, entidad, "FALLIDO: " + e.getMessage());
            throw e;
        }
    }
}
