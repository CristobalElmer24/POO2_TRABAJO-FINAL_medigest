package pe.medigest.web;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Sesiones en memoria: un código aleatorio (cookie) por persona conectada. Se pierden al apagar el servidor. */
public class Sesiones {

    public static final String COOKIE = "MG_SESION";

    private final Map<String, Sesion> activas = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();

    public String crear(Sesion sesion) {
        byte[] bytes = new byte[24];
        azar.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        activas.put(token, sesion);
        return token;
    }

    public Sesion buscar(String token) {
        return token == null ? null : activas.get(token);
    }

    public void cerrar(Sesion sesion) {
        activas.values().remove(sesion);
    }

    public int cantidad() { return activas.size(); }
}
