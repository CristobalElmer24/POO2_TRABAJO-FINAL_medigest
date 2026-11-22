package pe.medigest.inventario;

import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.evento.BusEventosInventario;
import pe.medigest.inventario.evento.EventoLoteIngresado;
import pe.medigest.inventario.evento.EventoStockAjustado;
import pe.medigest.inventario.regla.ConfiguracionInventario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Reglas del módulo Inventario. Farmacia no toca las tablas de lotes: le pide todo a esta clase.
 *
 * Concurrencia: todo lo que lee o escribe stock se hace con ConexionBD.CANDADO, así el hilo de monitoreo
 * nunca lee el inventario a la mitad de una dispensación.
 */
public class ServicioInventario {

    public static final int DIAS_ALERTA_VENCIMIENTO = 30;

    private final ConexionBD bd = ConexionBD.getInstancia();
    private final ProductoDAO productoDAO = new ProductoDAOJdbc();
    private final LoteDAO loteDAO = new LoteDAOJdbc();
    private final MovimientoDAO movimientoDAO = new MovimientoDAOJdbc();

    // Observer: CopyOnWriteArrayList porque el hilo de fondo recorre la lista mientras la interfaz puede agregar vistas
    private final List<ObservadorAlertas> observadores = new CopyOnWriteArrayList<>();
    private final Map<String, AlertaInventario> alertasActuales = new LinkedHashMap<>();

    private volatile EstrategiaSeleccionLote estrategia = new SeleccionFEFO();

    private final BusEventosInventario eventos = new BusEventosInventario();
    private final GeneradorAlertas generadorAlertas =
            new GeneradorAlertas(new ConfiguracionInventario(DIAS_ALERTA_VENCIMIENTO, 30, 30));

    /** Bus donde se publican los eventos del inventario (ingresos y ajustes ya confirmados). */
    public BusEventosInventario getEventos() {
        return eventos;
    }

    // ------------------------------------------------------------ Observer

    public void registrarObservador(ObservadorAlertas observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ObservadorAlertas observador) {
        observadores.remove(observador);
    }

    /** Alertas que están activas ahora mismo (para las vistas que se acaban de abrir). */
    public List<AlertaInventario> getAlertasActuales() {
        synchronized (alertasActuales) {
            return new ArrayList<>(alertasActuales.values());
        }
    }

    // ------------------------------------------------------------ Strategy

    public void setEstrategia(EstrategiaSeleccionLote estrategia) {
        this.estrategia = estrategia;
    }

    public EstrategiaSeleccionLote getEstrategia() {
        return estrategia;
    }

    // ------------------------------------------------------------ Consultas

    /** Productos con sus lotes ya cargados. */
    public List<Producto> listarProductos() {
        synchronized (ConexionBD.CANDADO) {
            List<Producto> productos = productoDAO.listar();
            Map<Integer, Producto> porId = new HashMap<>();
            for (Producto p : productos) {
                porId.put(p.getId(), p);
            }
            for (LoteAlmacen lote : loteDAO.listar()) {
                Producto dueno = porId.get(lote.getProductoId());
                if (dueno != null) {
                    dueno.agregarLote(lote);
                }
            }
            return productos;
        }
    }

    public Producto buscarProducto(int productoId) {
        synchronized (ConexionBD.CANDADO) {
            Producto p = productoDAO.buscarPorId(productoId);
            if (p == null) {
                throw new ReglaNegocioException("El producto no existe.");
            }
            p.setLotes(loteDAO.listarPorProducto(productoId));
            return p;
        }
    }

    public List<MovimientoInventario> listarMovimientos(int cantidad) {
        synchronized (ConexionBD.CANDADO) {
            return movimientoDAO.listarUltimos(cantidad);
        }
    }

    /** Unidades que realmente se pueden dispensar (sin contar lotes vencidos). */
    public int stockUtilizable(int productoId) {
        return buscarProducto(productoId).stockUtilizable();
    }

    /** True si hay stock en lotes vigentes para esa cantidad (según la estrategia activa). */
    public boolean verificarStock(int productoId, int cantidad) {
        synchronized (ConexionBD.CANDADO) {
            try {
                estrategia.seleccionar(loteDAO.listarPorProducto(productoId), cantidad);
                return true;
            } catch (ReglaNegocioException e) {
                return false;
            }
        }
    }

    // ------------------------------------------------------------ Catálogo de productos

    public void registrarProducto(Producto producto) {
        producto.validar();
        bd.enTransaccionVoid(() -> {
            if (productoDAO.buscarPorCodigo(producto.getCodigo()) != null) {
                throw new ReglaNegocioException("Ya existe un producto con el código " + producto.getCodigo() + ".");
            }
            productoDAO.insertar(producto);
        });
    }

    public void actualizarProducto(Producto producto) {
        producto.validar();
        bd.enTransaccionVoid(() -> {
            Producto mismoCodigo = productoDAO.buscarPorCodigo(producto.getCodigo());
            if (mismoCodigo != null && mismoCodigo.getId() != producto.getId()) {
                throw new ReglaNegocioException("Ya existe otro producto con el código " + producto.getCodigo() + ".");
            }
            productoDAO.actualizar(producto);
        });
    }

    public void eliminarProducto(int productoId) {
        bd.enTransaccionVoid(() -> {
            if (!loteDAO.listarPorProducto(productoId).isEmpty()) {
                throw new ReglaNegocioException("No se puede eliminar: el producto ya tiene lotes registrados.");
            }
            productoDAO.eliminar(productoId);
        });
    }

    // ------------------------------------------------------------ Movimientos de stock

    /** Ingreso de mercadería. Si el número de lote ya existe para ese producto, suma al mismo lote. */
    public LoteAlmacen registrarEntrada(int productoId, String nroLote, LocalDate fechaVencimiento,
                                        int cantidad, String motivo) {
        if (nroLote == null || nroLote.trim().isEmpty()) {
            throw new ReglaNegocioException("El número de lote es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de la entrada debe ser mayor a 0.");
        }
        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new LoteVencidoException("No se puede ingresar un lote que ya está vencido.");
        }

        LoteAlmacen resultado = bd.enTransaccion(() -> {
            buscarProducto(productoId); // lanza error si el producto no existe

            LoteAlmacen lote = loteDAO.buscarPorProductoYNroLote(productoId, nroLote.trim());
            if (lote == null) {
                lote = new LoteAlmacen(0, productoId, nroLote.trim(), fechaVencimiento, 0);
                loteDAO.insertar(lote);
            } else if (!lote.getFechaVencimiento().equals(fechaVencimiento)) {
                throw new ReglaNegocioException("El lote " + lote.getNroLote() + " ya existe con otra fecha de vencimiento ("
                        + lote.getFechaVencimiento() + ").");
            }
            aplicarMovimiento(MovimientoInventario.ENTRADA, lote, cantidad, motivo);
            return lote;
        });
        eventos.publicar(new EventoLoteIngresado(productoId, resultado.getNroLote(), cantidad));
        return resultado;
    }

    /** Corrección manual: diferencia positiva suma, negativa resta. */
    public void registrarAjuste(int loteId, int diferencia, String motivo) {
        bd.enTransaccionVoid(() -> {
            LoteAlmacen lote = loteDAO.buscarPorId(loteId);
            if (lote == null) {
                throw new ReglaNegocioException("El lote no existe.");
            }
            aplicarMovimiento(MovimientoInventario.AJUSTE, lote, diferencia, motivo);
        });
        eventos.publicar(new EventoStockAjustado(loteId, diferencia, motivo));
    }

    /**
     * Saca stock de un producto usando la estrategia activa (FEFO por defecto).
     * Puede repartir la cantidad entre varios lotes. Devuelve cuánto se usó de cada lote.
     * Si ya hay una transacción abierta (como en la dispensación) se une a ella.
     */
    public Map<LoteAlmacen, Integer> descontarStock(int productoId, int cantidad, String motivo) {
        return bd.enTransaccion(() -> {
            List<LoteAlmacen> lotes = loteDAO.listarPorProducto(productoId);
            List<LoteAlmacen> elegidos = estrategia.seleccionar(lotes, cantidad);

            Map<LoteAlmacen, Integer> usados = new LinkedHashMap<>();
            int restante = cantidad;
            for (LoteAlmacen lote : elegidos) {
                int aDescontar = Math.min(lote.getCantidadDisponible(), restante);
                aplicarMovimiento(MovimientoInventario.SALIDA, lote, aDescontar, motivo);
                usados.put(lote, aDescontar);
                restante -= aDescontar;
            }
            return usados;
        });
    }

    /** Devuelve unidades a un lote (se usa al anular un ticket de farmacia). */
    public void reponerStock(int loteId, int cantidad, String motivo) {
        bd.enTransaccionVoid(() -> {
            LoteAlmacen lote = loteDAO.buscarPorId(loteId);
            if (lote == null) {
                throw new ReglaNegocioException("El lote no existe.");
            }
            aplicarMovimiento(MovimientoInventario.ENTRADA, lote, cantidad, motivo);
        });
    }

    /** Crea el movimiento con la fábrica, lo aplica al lote en memoria y guarda ambos cambios. */
    private void aplicarMovimiento(String tipo, LoteAlmacen lote, int cantidad, String motivo) {
        MovimientoInventario movimiento = MovimientoFactory.crear(tipo, lote.getId(), cantidad, motivo);
        movimiento.aplicar(lote);
        loteDAO.actualizarCantidad(lote.getId(), lote.getCantidadDisponible());
        movimientoDAO.insertar(movimiento);
    }

    // ------------------------------------------------------------ Alertas

    /** Calcula las alertas de este momento: stock bajo, lotes por vencer y lotes vencidos. */
    public List<AlertaInventario> calcularAlertas() {
        return generadorAlertas.generar(listarProductos(), LocalDate.now());
    }

    /**
     * Revisa el inventario y avisa a los observadores solo de lo que cambió
     * (alertas nuevas, alertas con mensaje distinto y alertas que ya se resolvieron).
     * Lo llama el hilo de fondo cada cierto tiempo y también la interfaz cuando hace falta.
     */
    public void generarAlertas() {
        List<AlertaInventario> calculadas = calcularAlertas();
        List<AlertaInventario> paraAvisar = new ArrayList<>();
        List<String> resueltas = new ArrayList<>();

        synchronized (alertasActuales) {
            Set<String> clavesNuevas = new HashSet<>();
            for (AlertaInventario alerta : calculadas) {
                clavesNuevas.add(alerta.getClave());
                AlertaInventario anterior = alertasActuales.get(alerta.getClave());
                if (anterior == null || !anterior.getMensaje().equals(alerta.getMensaje())) {
                    alertasActuales.put(alerta.getClave(), alerta);
                    paraAvisar.add(alerta);
                }
            }
            Iterator<String> it = alertasActuales.keySet().iterator();
            while (it.hasNext()) {
                String clave = it.next();
                if (!clavesNuevas.contains(clave)) {
                    resueltas.add(clave);
                    it.remove();
                }
            }
        }

        // se avisa fuera del synchronized para no bloquear a nadie mientras las vistas reaccionan
        for (AlertaInventario alerta : paraAvisar) {
            for (ObservadorAlertas obs : observadores) {
                try {
                    obs.actualizar(alerta);
                } catch (RuntimeException e) {
                    System.err.println("Un observador falló: " + e.getMessage());
                }
            }
        }
        for (String clave : resueltas) {
            for (ObservadorAlertas obs : observadores) {
                try {
                    obs.alertaResuelta(clave);
                } catch (RuntimeException e) {
                    System.err.println("Un observador falló: " + e.getMessage());
                }
            }
        }
    }
}
