package pe.medigest.farmacia.servicio;

import pe.medigest.comun.ReglaNegocioException;

/** Convierte el id numérico de un ticket en el número que se imprime ("TK-000123") y viceversa. */
public final class NumeradorTicket {

    private static final String PREFIJO = "TK-";

    private NumeradorTicket() { }

    public static String formatear(int idTicket) {
        if (idTicket <= 0) {
            throw new ReglaNegocioException("El id del ticket debe ser mayor a 0.");
        }
        return String.format("%s%06d", PREFIJO, idTicket);
    }

    public static int extraer(String numero) {
        if (numero == null || !numero.trim().toUpperCase().startsWith(PREFIJO)) {
            throw new ReglaNegocioException("El número de ticket debe empezar con " + PREFIJO + ".");
        }
        try {
            return Integer.parseInt(numero.trim().substring(PREFIJO.length()));
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El número de ticket '" + numero + "' no es válido.");
        }
    }
}
