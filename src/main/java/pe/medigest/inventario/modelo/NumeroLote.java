package pe.medigest.inventario.modelo;

import pe.medigest.comun.ReglaNegocioException;

import java.util.Objects;

/** Número de lote tal como lo imprime el laboratorio: no vacío, hasta 30 caracteres, en mayúsculas. */
public final class NumeroLote {

    private final String valor;

    private NumeroLote(String valor) {
        this.valor = valor;
    }

    public static NumeroLote de(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El número de lote es obligatorio.");
        }
        String limpio = texto.trim().toUpperCase();
        if (limpio.length() > 30) {
            throw new ReglaNegocioException("El número de lote no puede pasar de 30 caracteres.");
        }
        return new NumeroLote(limpio);
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof NumeroLote && ((NumeroLote) otro).valor.equals(valor);
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return valor; }
}
