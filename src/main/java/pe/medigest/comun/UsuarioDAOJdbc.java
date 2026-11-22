package pe.medigest.comun;

public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public Usuario autenticar(String nombreUsuario, String hashClave) {
        return JdbcUtil.consultarUno(
                "SELECT id, nombre_usuario, rol FROM usuario WHERE nombre_usuario = ? AND hash_clave = ?",
                rs -> new Usuario(rs.getInt("id"), rs.getString("nombre_usuario"), rs.getString("rol")),
                nombreUsuario, hashClave);
    }

    @Override
    public void insertar(String nombreUsuario, String hashClave, String rol) {
        JdbcUtil.insertar("INSERT INTO usuario (nombre_usuario, hash_clave, rol) VALUES (?,?,?)",
                nombreUsuario, hashClave, rol);
    }

    @Override
    public int contar() {
        Integer total = JdbcUtil.consultarUno("SELECT COUNT(*) AS total FROM usuario", rs -> rs.getInt("total"));
        return total == null ? 0 : total;
    }
}
