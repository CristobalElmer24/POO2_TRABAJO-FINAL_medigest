package pe.medigest.inventario.modelo;

import pe.medigest.inventario.AlertaInventario;

/** Qué tan urgente es una alerta; sirve para ordenarlas en pantalla. */
public enum SeveridadAlerta {
    INFORMATIVA(1),
    ADVERTENCIA(2),
    CRITICA(3);

    private final int peso;

    SeveridadAlerta(int peso) {
        this.peso = peso;
    }

    public int getPeso() { return peso; }

    public static SeveridadAlerta de(AlertaInventario.Tipo tipo) {
        switch (tipo) {
            case VENCIDO:
                return CRITICA;
            case STOCK_BAJO:
                return ADVERTENCIA;
            default:
                return INFORMATIVA;
        }
    }
}
