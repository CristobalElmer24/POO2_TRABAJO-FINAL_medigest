package pe.medigest.inventario.modelo;

import pe.medigest.inventario.excepcion.CodigoProductoInvalidoException;

import java.util.Objects;
import java.util.regex.Pattern;

/** Código de producto: letras, números y guiones, entre 2 y 20 caracteres. Siempre en mayúsculas. */
public final class CodigoProducto {

    private static final Pattern FORMATO = Pattern.compile("^[A-Z0-9][A-Z0-9-]{1,19}$");

    private final String valor;

    private CodigoProducto(String valor) {
        this.valor = valor;
    }

    public static CodigoProducto de(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new CodigoProductoInvalidoException("El código del producto es obligatorio.");
        }
        String limpio = texto.trim().toUpperCase();
        if (!FORMATO.matcher(limpio).matches()) {
            throw new CodigoProductoInvalidoException("El código '" + texto.trim()
                    + "' no es válido: use 2 a 20 letras, números o guiones.");
        }
        return new CodigoProducto(limpio);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof CodigoProducto && ((CodigoProducto) otro).valor.equals(valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
