package pe.medigest.inventario;

/** Factory: arma las alertas con sus claves y mensajes en un solo lugar. */
public final class AlertaFactory {

    private AlertaFactory() { }

    public static AlertaInventario stockBajo(Producto p, int utilizable) {
        return new AlertaInventario(AlertaInventario.Tipo.STOCK_BAJO, "STOCK-" + p.getId(), p.getNombre(), null,
                "Stock bajo: quedan " + utilizable + " unidades utilizables (mínimo " + p.getStockMinimo() + ").");
    }

    public static AlertaInventario loteVencido(Producto p, LoteAlmacen lote) {
        return new AlertaInventario(AlertaInventario.Tipo.VENCIDO, "VENC-" + lote.getId(), p.getNombre(),
                lote.getNroLote(), "Lote vencido el " + lote.getFechaVencimiento() + " ("
                + lote.getCantidadDisponible() + " unidades). No se puede dispensar.");
    }

    public static AlertaInventario porVencer(Producto p, LoteAlmacen lote, long dias) {
        return new AlertaInventario(AlertaInventario.Tipo.POR_VENCER, "POR-" + lote.getId(), p.getNombre(),
                lote.getNroLote(), "Vence en " + dias + " días (" + lote.getFechaVencimiento() + ").");
    }
}
