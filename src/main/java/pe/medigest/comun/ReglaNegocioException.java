package pe.medigest.comun;

/** Se lanza cuando se intenta una operación que rompe una regla del negocio. El mensaje se muestra al usuario. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
