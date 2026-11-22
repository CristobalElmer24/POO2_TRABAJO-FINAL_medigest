package pe.medigest.inventario.evento;

/** Se corrigió manualmente el stock de un lote. */
public class EventoStockAjustado extends EventoInventario {
    private final int loteId;
    private final int diferencia;
    private final String motivo;

    public EventoStockAjustado(int loteId, int diferencia, String motivo) {
        this.loteId = loteId;
        this.diferencia = diferencia;
        this.motivo = motivo;
    }

    public int getLoteId() { return loteId; }

    public int getDiferencia() { return diferencia; }

    public String getMotivo() { return motivo; }

    @Override
    public String descripcion() {
        return "Ajuste de " + (diferencia > 0 ? "+" : "") + diferencia + " en el lote " + loteId + ": " + motivo;
    }
}
