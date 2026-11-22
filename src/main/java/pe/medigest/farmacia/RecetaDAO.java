package pe.medigest.farmacia;

public interface RecetaDAO {
    /** Busca por código e incluye sus detalles. Devuelve null si no existe. */
    Receta buscarPorCodigo(String codigo);

    /** Guarda la receta con sus detalles. */
    void insertar(Receta receta);

    void actualizarEstado(int recetaId, String estado);
}
