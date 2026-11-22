package pe.medigest.farmacia.modelo;

/** Por qué se rechazó una dispensación. Sirve para la bitácora y para mostrar mensajes claros. */
public enum MotivoRechazo {
    RECETA_INEXISTENTE("La receta no existe"),
    YA_DISPENSADA("La receta ya fue dispensada"),
    RECETA_VENCIDA("La receta está vencida"),
    SIN_MEDICAMENTOS("La receta no tiene medicamentos"),
    CANTIDAD_INVALIDA("Hay una cantidad inválida en la receta"),
    MEDICAMENTO_REPETIDO("Un medicamento aparece repetido en la receta"),
    STOCK_INSUFICIENTE("No hay stock suficiente"),
    LOTE_VENCIDO("Solo hay lotes vencidos"),
    SIN_PERMISO("El rol del usuario no tiene permiso");

    private final String descripcion;

    MotivoRechazo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }
}
