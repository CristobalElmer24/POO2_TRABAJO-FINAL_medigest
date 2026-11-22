package pe.medigest.inventario;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Hilo de fondo que revisa el inventario cada cierto tiempo sin congelar la interfaz. */
public class MonitorInventario {

    private final ServicioInventario servicio;
    private final int segundos;
    private ScheduledExecutorService ejecutor;

    public MonitorInventario(ServicioInventario servicio, int segundos) {
        this.servicio = servicio;
        this.segundos = segundos;
    }

    public MonitorInventario(ServicioInventario servicio) {
        this(servicio, 15);
    }

    public synchronized void iniciar() {
        if (ejecutor != null) {
            return; // ya estaba corriendo
        }
        ejecutor = Executors.newSingleThreadScheduledExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "monitor-inventario");
            hilo.setDaemon(true); // no impide que el programa se cierre
            return hilo;
        });
        ejecutor.scheduleAtFixedRate(this::revisar, 0, segundos, TimeUnit.SECONDS);
    }

    public synchronized void detener() {
        if (ejecutor != null) {
            ejecutor.shutdownNow();
            ejecutor = null;
        }
    }

    /**
     * Ojo: si una tarea de scheduleAtFixedRate lanza una excepción, Java cancela todas las ejecuciones siguientes.
     * Por eso se atrapa todo aquí, para que un error puntual no apague el monitoreo.
     */
    private void revisar() {
        try {
            servicio.generarAlertas();
        } catch (Exception e) {
            System.err.println("Error en el monitoreo de inventario: " + e.getMessage());
        }
    }
}
