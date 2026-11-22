package pe.medigest.inventario.modelo;

/** Unidad en la que se cuenta un producto. */
public enum UnidadMedida {
    TABLETA("tableta", "tab."),
    CAPSULA("cápsula", "cáps."),
    FRASCO("frasco", "fco."),
    AMPOLLA("ampolla", "amp."),
    SOBRE("sobre", "sob.");

    private final String nombre;
    private final String abreviatura;

    UnidadMedida(String nombre, String abreviatura) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
    }

    public String getNombre() { return nombre; }

    public String getAbreviatura() { return abreviatura; }

    /** "10 tab." o "1 tab." según la cantidad. */
    public String formatear(int cantidad) {
        return cantidad + " " + abreviatura;
    }
}
