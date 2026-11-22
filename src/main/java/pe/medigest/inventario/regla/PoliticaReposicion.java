package pe.medigest.inventario.regla;

import pe.medigest.inventario.Producto;

/** Strategy para decidir cuánto reponer de un producto. */
public interface PoliticaReposicion {

    SugerenciaReposicion sugerir(Producto producto);
}
