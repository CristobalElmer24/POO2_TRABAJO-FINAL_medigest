package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

import java.time.LocalDate;

/** Un lote de un producto: tiene su propio número, fecha de vencimiento y cantidad. */
public class LoteAlmacen {

    private int id;
    private final int productoId;
    private final String nroLote;
    private final LocalDate fechaVencimiento;
    private int cantidadDisponible;

    public LoteAlmacen(int id, int productoId, String nroLote, LocalDate fechaVencimiento, int cantidadDisponible) {
        this.id = id;
        this.productoId = productoId;
        this.nroLote = nroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getProductoId() { return productoId; }

    public String getNroLote() { return nroLote; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public int getCantidadDisponible() { return cantidadDisponible; }

    /** Un lote que vence hoy todavía se puede usar hoy; está vencido desde mañana. */
    public boolean estaVencido() {
        return estaVencido(LocalDate.now());
    }

    public boolean estaVencido(LocalDate hoy) {
        return fechaVencimiento.isBefore(hoy);
    }

    /** True si todavía no venció pero vence dentro de los próximos "dias" días. */
    public boolean estaPorVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        return !estaVencido(hoy) && !fechaVencimiento.isAfter(hoy.plusDays(dias));
    }

    public void descontar(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a descontar debe ser mayor a 0.");
        }
        if (cantidad > cantidadDisponible) {
            throw new StockInsuficienteException("El lote " + nroLote + " solo tiene " + cantidadDisponible + " unidades.");
        }
        cantidadDisponible -= cantidad;
    }

    public void agregar(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a agregar debe ser mayor a 0.");
        }
        cantidadDisponible += cantidad;
    }
}
