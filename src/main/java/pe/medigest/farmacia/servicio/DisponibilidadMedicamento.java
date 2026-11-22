package pe.medigest.farmacia.servicio;

import pe.medigest.inventario.Producto;
import pe.medigest.inventario.modelo.NivelStock;
import pe.medigest.inventario.regla.ClasificadorNivelStock;

/** Lo que farmacia necesita saber de un medicamento al consultarlo: cuánto se puede dispensar y qué tan escaso está. */
public class DisponibilidadMedicamento {
    private final int productoId;
    private final String codigo;
    private final String nombre;
    private final String presentacion;
    private final int unidadesDispensables;
    private final NivelStock nivel;

    public DisponibilidadMedicamento(Producto producto, ClasificadorNivelStock clasificador) {
        this.productoId = producto.getId();
        this.codigo = producto.getCodigo();
        this.nombre = producto.getNombre();
        this.presentacion = producto.getPresentacion();
        this.unidadesDispensables = producto.stockUtilizable();
        this.nivel = clasificador.clasificar(unidadesDispensables, producto.getStockMinimo());
    }

    public boolean estaDisponible() { return unidadesDispensables > 0; }

    public boolean alcanzaPara(int cantidad) { return unidadesDispensables >= cantidad; }

    public int getProductoId() { return productoId; }

    public String getCodigo() { return codigo; }

    public String getNombre() { return nombre; }

    public String getPresentacion() { return presentacion; }

    public int getUnidadesDispensables() { return unidadesDispensables; }

    public NivelStock getNivel() { return nivel; }
}
