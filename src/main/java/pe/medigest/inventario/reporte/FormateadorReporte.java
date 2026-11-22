package pe.medigest.inventario.reporte;

/** Strategy: convierte una TablaReporte en texto con un formato concreto. */
public interface FormateadorReporte {
    String formatear(TablaReporte tabla);

    String getExtension();
}
