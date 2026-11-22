package pe.medigest.comun;

public interface UsuarioDAO {
    /** Devuelve el usuario si el nombre y el hash coinciden; si no, null. */
    Usuario autenticar(String nombreUsuario, String hashClave);

    void insertar(String nombreUsuario, String hashClave, String rol);

    int contar();
}
