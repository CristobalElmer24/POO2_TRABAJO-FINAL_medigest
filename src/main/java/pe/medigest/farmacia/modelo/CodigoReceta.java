package pe.medigest.farmacia.modelo;

import pe.medigest.farmacia.RecetaInvalidaException;

import java.util.Objects;
import java.util.regex.Pattern;

/** Código de receta como "REC-001": letras, números y guiones, siempre en mayúsculas. */
public final class CodigoReceta {

    private static final Pattern FORMATO = Pattern.compile("^[A-Z0-9][A-Z0-9-]{1,19}$");

    private final String valor;

    private CodigoReceta(String valor) {
        this.valor = valor;
    }

    public static CodigoReceta de(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new RecetaInvalidaException("El código de la receta es obligatorio.");
        }
        String limpio = texto.trim().toUpperCase();
        if (!FORMATO.matcher(limpio).matches()) {
            throw new RecetaInvalidaException("El código '" + texto.trim() + "' no tiene un formato válido.");
        }
        return new CodigoReceta(limpio);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof CodigoReceta && ((CodigoReceta) otro).valor.equals(valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
