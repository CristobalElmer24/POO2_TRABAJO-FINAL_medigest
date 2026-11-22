package pe.medigest.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import pe.medigest.comun.AccesoDatosException;
import pe.medigest.comun.ReglaNegocioException;
import pe.medigest.inventario.ServicioInventario;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Servidor web incluido en el JDK (sin Tomcat ni Spring): sirve la carpeta resources/web y la API /api/*.
 * Atiende de a una petición por vez, porque la base SQLite del prototipo usa una sola conexión.
 */
public class ServidorWeb {

    private static final Map<String, String> TIPOS = Map.of(
            "html", "text/html; charset=utf-8", "css", "text/css; charset=utf-8",
            "js", "text/javascript; charset=utf-8", "svg", "image/svg+xml", "png", "image/png", "ico", "image/x-icon");

    private final HttpServer servidor;
    private final RutasApi api;

    public ServidorWeb(ServicioInventario servicio, int puerto, boolean abiertoALaRed) throws IOException {
        this.api = new RutasApi(servicio, new Sesiones());
        InetAddress direccion = abiertoALaRed ? InetAddress.getByName("0.0.0.0") : InetAddress.getLoopbackAddress();
        this.servidor = HttpServer.create(new InetSocketAddress(direccion, puerto), 0);
        this.servidor.createContext("/", this::atender);
        this.servidor.setExecutor(Executors.newSingleThreadExecutor());
    }

    public void iniciar() { servidor.start(); }

    public void detener() { servidor.stop(0); }

    public int getPuerto() { return servidor.getAddress().getPort(); }

    private void atender(HttpExchange ex) throws IOException {
        try {
            String ruta = ex.getRequestURI().getPath();
            if (ruta.startsWith("/api/")) {
                atenderApi(ex, ruta);
            } else {
                atenderArchivo(ex, ruta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responder(ex, 500, Json.aTexto(Json.obj("error", "Error interno del servidor.")), "application/json; charset=utf-8");
        } finally {
            ex.close();
        }
    }

    // ------------------------------------------------------------ API

    private void atenderApi(HttpExchange ex, String ruta) throws IOException {
        String metodo = ex.getRequestMethod();
        int codigo = 200;
        Object cuerpo;
        try {
            if (!api.existe(metodo, ruta)) {
                throw new ErrorApi(404, "Ruta no encontrada: " + metodo + " " + ruta);
            }
            // Protección básica contra peticiones de otros sitios: las acciones exigen esta cabecera (solo la manda nuestra página)
            if ("POST".equals(metodo) && ex.getRequestHeaders().getFirst("X-MediGest") == null) {
                throw new ErrorApi(403, "Petición no permitida.");
            }
            Sesion sesion = api.getSesiones().buscar(leerCookie(ex));
            if (sesion == null && !api.esPublica(metodo, ruta)) {
                throw new ErrorApi(401, "Debe iniciar sesión.");
            }
            Object resultado = api.buscar(metodo, ruta).manejar(sesion, Parametros.de(ex));
            if (resultado instanceof Map && ((Map<?, ?>) resultado).containsKey("token") && "/api/login".equals(ruta)) {
                String token = (String) ((Map<?, ?>) resultado).remove("token");
                ex.getResponseHeaders().add("Set-Cookie", Sesiones.COOKIE + "=" + token + "; Path=/; HttpOnly; SameSite=Strict");
            }
            cuerpo = resultado;
        } catch (ErrorApi e) {
            codigo = e.getCodigo();
            cuerpo = Json.obj("error", e.getMessage());
        } catch (ReglaNegocioException e) {
            codigo = 400;
            cuerpo = Json.obj("error", e.getMessage());
        } catch (AccesoDatosException e) {
            e.printStackTrace();
            codigo = 500;
            cuerpo = Json.obj("error", "Error de base de datos. Revise la consola del servidor.");
        } catch (Exception e) {
            e.printStackTrace();
            codigo = 500;
            cuerpo = Json.obj("error", "Error inesperado: " + e.getClass().getSimpleName());
        }
        responder(ex, codigo, Json.aTexto(cuerpo), "application/json; charset=utf-8");
    }

    private String leerCookie(HttpExchange ex) {
        String cabecera = ex.getRequestHeaders().getFirst("Cookie");
        if (cabecera == null) {
            return null;
        }
        for (String parte : cabecera.split(";")) {
            String[] kv = parte.trim().split("=", 2);
            if (kv.length == 2 && Sesiones.COOKIE.equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    // ------------------------------------------------------------ archivos estáticos (carpeta resources/web)

    private void atenderArchivo(HttpExchange ex, String ruta) throws IOException {
        if (!"GET".equals(ex.getRequestMethod())) {
            responder(ex, 405, "Método no permitido", "text/plain; charset=utf-8");
            return;
        }
        String archivo = "/".equals(ruta) ? "/index.html" : ruta;
        if (archivo.contains("..") || archivo.contains("\\")) {
            responder(ex, 400, "Ruta no válida", "text/plain; charset=utf-8");
            return;
        }
        try (InputStream in = ServidorWeb.class.getResourceAsStream("/web" + archivo)) {
            if (in == null) {
                responder(ex, 404, "No encontrado: " + archivo, "text/plain; charset=utf-8");
                return;
            }
            String ext = archivo.substring(archivo.lastIndexOf('.') + 1);
            ex.getResponseHeaders().add("Cache-Control", "no-cache");
            responder(ex, 200, in.readAllBytes(), TIPOS.getOrDefault(ext, "application/octet-stream"));
        }
    }

    private void responder(HttpExchange ex, int codigo, String texto, String tipo) throws IOException {
        responder(ex, codigo, texto.getBytes(StandardCharsets.UTF_8), tipo);
    }

    private void responder(HttpExchange ex, int codigo, byte[] bytes, String tipo) throws IOException {
        ex.getResponseHeaders().set("Content-Type", tipo);
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}
