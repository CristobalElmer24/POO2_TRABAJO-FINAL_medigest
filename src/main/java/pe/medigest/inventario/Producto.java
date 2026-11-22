package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Medicamento o insumo del catálogo. Sus lotes se cargan desde ServicioInventario. */
public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private String presentacion;
    private int stockMinimo;
    private final List<LoteAlmacen> lotes = new ArrayList<>();

    public Producto(int id, String codigo, String nombre, String presentacion, int stockMinimo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.stockMinimo = stockMinimo;
    }

    /** Para productos nuevos (todavía sin id). */
    public Producto(String codigo, String nombre, String presentacion, int stockMinimo) {
        this(0, codigo, nombre, presentacion, stockMinimo);
    }

    /** Revisa que los datos tengan sentido antes de guardarlos. */
    public void validar() {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código del producto es obligatorio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        }
        if (presentacion == null || presentacion.trim().isEmpty()) {
            throw new ReglaNegocioException("La presentación del producto es obligatoria.");
        }
        if (stockMinimo < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo.");
        }
    }

    /** Todas las unidades, incluso las de lotes vencidos. */
    public int stockTotal() {
        int total = 0;
        for (LoteAlmacen l : lotes) {
            total += l.getCantidadDisponible();
        }
        return total;
    }

    /** Solo las unidades de lotes que todavía no vencieron (las que realmente se pueden dispensar). */
    public int stockUtilizable() {
        int total = 0;
        for (LoteAlmacen l : lotes) {
            if (!l.estaVencido()) {
                total += l.getCantidadDisponible();
            }
        }
        return total;
    }

    public void agregarLote(LoteAlmacen lote) { lotes.add(lote); }

    public void setLotes(List<LoteAlmacen> nuevos) {
        lotes.clear();
        lotes.addAll(nuevos);
    }

    public List<LoteAlmacen> getLotes() { return Collections.unmodifiableList(lotes); }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPresentacion() { return presentacion; }

    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public int getStockMinimo() { return stockMinimo; }

    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
}
