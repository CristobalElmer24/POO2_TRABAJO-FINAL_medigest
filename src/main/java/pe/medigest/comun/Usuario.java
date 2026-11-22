package pe.medigest.comun;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Usuario del sistema (servicio transversal de autenticación y roles). */
public class Usuario {

    // Roles
    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String FARMACIA = "FARMACIA";
    public static final String INVENTARIO = "INVENTARIO";

    // Acciones que se controlan por rol (consultar es libre para todos los roles)
    public static final String DISPENSAR = "DISPENSAR";
    public static final String ANULAR_TICKET = "ANULAR_TICKET";
    public static final String GESTIONAR_INVENTARIO = "GESTIONAR_INVENTARIO";
    public static final String VER_BITACORA = "VER_BITACORA";

    private final int id;
    private final String nombreUsuario;
    private final String rol;

    public Usuario(int id, String nombreUsuario, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

    public int getId() { return id; }

    public String getNombreUsuario() { return nombreUsuario; }

    public String getRol() { return rol; }

    /** El administrador puede todo; farmacia solo dispensa; inventario solo gestiona el inventario. */
    public boolean tienePermiso(String accion) {
        if (ADMINISTRADOR.equals(rol)) {
            return true;
        }
        switch (accion) {
            case DISPENSAR:
            case ANULAR_TICKET:
                return FARMACIA.equals(rol);
            case GESTIONAR_INVENTARIO:
                return INVENTARIO.equals(rol);
            default:
                return false;
        }
    }

    /** SHA-256 en hexadecimal. Las contraseñas nunca se guardan en texto plano. */
    public static String generarHash(String clave) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(clave.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
