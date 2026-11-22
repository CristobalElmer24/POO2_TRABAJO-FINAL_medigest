package pe.medigest.comun;

/** Error al leer o escribir en la base de datos (envuelve a SQLException para no llenar el código de try/catch). */
public class AccesoDatosException extends RuntimeException {
    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
