package pe.medigest.inventario.modelo;

/** Qué tan abastecido está un producto respecto de su stock mínimo. */
public enum NivelStock {
    SIN_STOCK(0),
    CRITICO(1),
    BAJO(2),
    NORMAL(3),
    ALTO(4);

    private final int prioridad;

    NivelStock(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getPrioridad() { return prioridad; }

    public boolean requiereReposicion() {
        return prioridad <= BAJO.prioridad;
    }
}
