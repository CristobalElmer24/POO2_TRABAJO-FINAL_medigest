package pe.medigest.comun;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Validaciones de lo que escribe el usuario en los formularios. Siempre lanzan mensajes claros. */
public final class Validaciones {

    private Validaciones() { }

    public static String textoObligatorio(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo '" + campo + "' es obligatorio.");
        }
        return texto.trim();
    }

    public static int entero(String texto, String campo) {
        String limpio = textoObligatorio(texto, campo);
        try {
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El campo '" + campo + "' debe ser un número entero.");
        }
    }

    public static int enteroPositivo(String texto, String campo) {
        int valor = entero(texto, campo);
        if (valor <= 0) {
            throw new ReglaNegocioException("El campo '" + campo + "' debe ser mayor a 0.");
        }
        return valor;
    }

    public static LocalDate fecha(String texto, String campo) {
        String limpio = textoObligatorio(texto, campo);
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("El campo '" + campo + "' debe tener el formato AAAA-MM-DD (ejemplo: 2027-05-31).");
        }
    }
}
