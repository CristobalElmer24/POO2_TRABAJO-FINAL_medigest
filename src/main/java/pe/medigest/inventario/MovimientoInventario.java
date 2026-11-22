package pe.medigest.inventario;

import java.time.LocalDateTime;

/**
 * Movimiento de stock sobre un lote. Cada tipo sabe cómo aplicarse al lote (polimorfismo).
 */
public abstract class MovimientoInventario {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";
    public static final String AJUSTE = "AJUSTE";

    private int id;
    protected final int loteId;
    protected final int cantidad;
    protected final String tipo;
    protected final String motivo;
    private LocalDateTime fecha = LocalDateTime.now();
    private String detalleLote; // "Paracetamol / LOTE-A", solo para mostrar en pantalla

    protected MovimientoInventario(int loteId, int cantidad, String tipo, String motivo) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.motivo = motivo;
    }

    /** Cambia la cantidad del lote según el tipo de movimiento. */
    public abstract void aplicar(LoteAlmacen lote);

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getLoteId() { return loteId; }

    /** En ENTRADA y SALIDA es positiva; en AJUSTE puede ser negativa (diferencia). */
    public int getCantidad() { return cantidad; }

    public String getTipo() { return tipo; }

    public String getMotivo() { return motivo; }

    public LocalDateTime getFecha() { return fecha; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDetalleLote() { return detalleLote; }

    public void setDetalleLote(String detalleLote) { this.detalleLote = detalleLote; }
}
