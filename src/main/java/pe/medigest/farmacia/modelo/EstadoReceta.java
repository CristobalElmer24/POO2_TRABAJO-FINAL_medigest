package pe.medigest.farmacia.modelo;

import pe.medigest.farmacia.Receta;

/** Estados posibles de una receta desde el punto de vista de farmacia. */
public enum EstadoReceta {
    EMITIDA(Receta.EMITIDA),
    DISPENSADA(Receta.DISPENSADA);

    private final String codigo;

    EstadoReceta(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }

    public static EstadoReceta desde(String texto) {
        for (EstadoReceta e : values()) {
            if (e.codigo.equalsIgnoreCase(texto)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado de receta desconocido: " + texto);
    }
}
