package pe.medigest.inventario;

import java.time.LocalDateTime;

/** Aviso que genera el inventario: stock bajo, lote por vencer o lote vencido. */
public class AlertaInventario {

    public enum Tipo { STOCK_BAJO, POR_VENCER, VENCIDO }

    private final Tipo tipo;
    private final String clave; // identifica la alerta (ej. "STOCK-3"), para no repetirla en cada revisión
    private final String producto;
    private final String nroLote; // null en alertas de stock bajo
    private final String mensaje;
    private final LocalDateTime fecha = LocalDateTime.now();

    public AlertaInventario(Tipo tipo, String clave, String producto, String nroLote, String mensaje) {
        this.tipo = tipo;
        this.clave = clave;
        this.producto = producto;
        this.nroLote = nroLote;
        this.mensaje = mensaje;
    }

    public Tipo getTipo() { return tipo; }

    public String getClave() { return clave; }

    public String getProducto() { return producto; }

    public String getNroLote() { return nroLote; }

    public String getMensaje() { return mensaje; }

    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return "[" + tipo + "] " + producto + (nroLote != null ? " (" + nroLote + ")" : "") + ": " + mensaje;
    }
}
