package pe.medigest.farmacia.reporte;

/** Un medicamento y cuántas unidades se dispensaron en total. */
public class ProductoDispensado {
    private final String nombre;
    private final int unidades;

    public ProductoDispensado(String nombre, int unidades) {
        this.nombre = nombre;
        this.unidades = unidades;
    }

    public String getNombre() { return nombre; }

    public int getUnidades() { return unidades; }
}
