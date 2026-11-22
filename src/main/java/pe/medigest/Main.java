package pe.medigest;

import pe.medigest.inventario.MonitorInventario;
import pe.medigest.inventario.ServicioInventario;
import pe.medigest.web.ServidorWeb;

import java.awt.Desktop;
import java.net.URI;

/**
 * Arranca MediGest como aplicación web: prepara la base de datos y enciende el servidor.
 * Uso: mvn compile exec:java   (opcional: -Dexec.args="9090" para otro puerto, o "9090 --red" para abrirlo a la red local)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int puerto = 8080;
        boolean red = false;
        for (String a : args) {
            if ("--red".equals(a)) {
                red = true;
            } else {
                puerto = Integer.parseInt(a);
            }
        }

        try {
            InicializadorBD.preparar();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar la base de datos: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        ServicioInventario inventario = new ServicioInventario();

        // Hilo de fondo: revisa stock y vencimientos cada 15 segundos
        MonitorInventario monitor = new MonitorInventario(inventario);
        monitor.iniciar();

        ServidorWeb servidor = new ServidorWeb(inventario, puerto, red);
        servidor.iniciar();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.detener();
            servidor.detener();
        }));

        String url = "http://localhost:" + servidor.getPuerto();
        System.out.println("MediGest está encendido en " + url + (red ? "  (abierto a la red local)" : ""));
        System.out.println("Usuarios de prueba: admin / farmacia / inventario (clave = usuario). Ctrl+C para apagar.");
        abrirNavegador(url);
    }

    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception e) {
            // si no se puede abrir solo, basta con escribir la dirección en el navegador
        }
    }
}
