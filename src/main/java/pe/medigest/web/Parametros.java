package pe.medigest.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Parámetros de una petición: los de la URL (?a=1) más los del cuerpo (formulario a=1&b=2). */
public class Parametros {

    private final Map<String, String> valores = new HashMap<>();

    public static Parametros de(HttpExchange ex) throws IOException {
        Parametros p = new Parametros();
        p.leer(ex.getRequestURI().getRawQuery());
        if ("POST".equals(ex.getRequestMethod())) {
            byte[] cuerpo = ex.getRequestBody().readNBytes(64 * 1024);
            p.leer(new String(cuerpo, StandardCharsets.UTF_8));
        }
        return p;
    }

    private void leer(String texto) {
        if (texto == null || texto.isEmpty()) {
            return;
        }
        for (String par : texto.split("&")) {
            int i = par.indexOf('=');
            String clave = decodificar(i < 0 ? par : par.substring(0, i));
            String valor = i < 0 ? "" : decodificar(par.substring(i + 1));
            valores.put(clave, valor);
        }
    }

    private static String decodificar(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    /** Devuelve el valor o "" si no vino (los controladores validan los obligatorios). */
    public String texto(String clave) {
        String v = valores.get(clave);
        return v == null ? "" : v.trim();
    }

    public int entero(String clave) {
        try {
            return Integer.parseInt(texto(clave));
        } catch (NumberFormatException e) {
            throw new pe.medigest.comun.ReglaNegocioException("El campo '" + clave + "' debe ser un número entero.");
        }
    }
}
