package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.modelo.MotivoRechazo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado de revisar una receta contra todas las reglas: qué falló y con qué mensaje. */
public class ResultadoValidacion {
    private final List<MotivoRechazo> motivos = new ArrayList<>();
    private final List<String> mensajes = new ArrayList<>();

    void agregar(MotivoRechazo motivo, String mensaje) {
        motivos.add(motivo);
        mensajes.add(mensaje);
    }

    public boolean esValida() { return motivos.isEmpty(); }

    public List<MotivoRechazo> getMotivos() { return Collections.unmodifiableList(motivos); }

    public List<String> getMensajes() { return Collections.unmodifiableList(mensajes); }

    public String resumen() {
        return esValida() ? "Receta válida." : String.join(" ", mensajes);
    }
}
