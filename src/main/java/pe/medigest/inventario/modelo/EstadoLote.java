package pe.medigest.inventario.modelo;

/** Situación de un lote en un momento dado. */
public enum EstadoLote {
    VIGENTE(true),
    POR_VENCER(true),
    VENCIDO(false),
    AGOTADO(false);

    private final boolean dispensable;

    EstadoLote(boolean dispensable) {
        this.dispensable = dispensable;
    }

    /** Solo los lotes vigentes o por vencer se pueden dispensar. */
    public boolean esDispensable() { return dispensable; }
}
