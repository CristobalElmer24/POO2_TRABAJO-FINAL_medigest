package pe.medigest.comun;

/** Controlador del inicio de sesión (así la ventana de login no toca la base de datos). */
public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOJdbc();

    public Usuario iniciarSesion(String nombreUsuario, String clave) {
        String nombre = Validaciones.textoObligatorio(nombreUsuario, "Usuario");
        String claveIngresada = Validaciones.textoObligatorio(clave, "Contraseña");

        Usuario usuario = usuarioDAO.autenticar(nombre, Usuario.generarHash(claveIngresada));
        if (usuario == null) {
            AuditoriaLogger.registrar(nombre, "LOGIN", "Sesión", "FALLIDO: credenciales incorrectas");
            throw new ReglaNegocioException("Usuario o contraseña incorrectos.");
        }
        AuditoriaLogger.registrar(usuario.getNombreUsuario(), "LOGIN", "Sesión", "EXITOSO");
        return usuario;
    }
}
