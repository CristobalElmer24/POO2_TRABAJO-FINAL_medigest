package pe.medigest.farmacia.modelo;

import pe.medigest.comun.ReglaNegocioException;

/** Nombre y documento del paciente al que pertenece una receta. */
public final class DatosPaciente {

    private final String nombre;
    private final DocumentoPaciente documento;

    public DatosPaciente(String nombre, DocumentoPaciente documento) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del paciente es obligatorio.");
        }
        if (documento == null) {
            throw new ReglaNegocioException("El documento del paciente es obligatorio.");
        }
        this.nombre = nombre.trim();
        this.documento = documento;
    }

    public String getNombre() { return nombre; }

    public DocumentoPaciente getDocumento() { return documento; }

    @Override
    public String toString() { return nombre + " (" + documento + ")"; }
}
