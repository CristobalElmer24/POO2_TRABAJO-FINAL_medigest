package pe.medigest.inventario.modelo;

import pe.medigest.inventario.excepcion.CantidadInvalidaException;

/** Cantidad de unidades, siempre mayor o igual a 0. Inmutable: sumar o restar devuelve otra Cantidad. */
public final class Cantidad implements Comparable<Cantidad> {

    public static final Cantidad CERO = new Cantidad(0);

    private final int unidades;

    private Cantidad(int unidades) {
        this.unidades = unidades;
    }

    public static Cantidad de(int unidades) {
        if (unidades < 0) {
            throw new CantidadInvalidaException("La cantidad no puede ser negativa (" + unidades + ").");
        }
        return new Cantidad(unidades);
    }

    public static Cantidad positiva(int unidades) {
        if (unidades <= 0) {
            throw new CantidadInvalidaException("La cantidad debe ser mayor a 0 (" + unidades + ").");
        }
        return new Cantidad(unidades);
    }

    public Cantidad sumar(Cantidad otra) {
        return new Cantidad(unidades + otra.unidades);
    }

    public Cantidad restar(Cantidad otra) {
        if (otra.unidades > unidades) {
            throw new CantidadInvalidaException("No se puede restar " + otra.unidades + " a " + unidades + ".");
        }
        return new Cantidad(unidades - otra.unidades);
    }

    public boolean esCero() { return unidades == 0; }

    public int getUnidades() { return unidades; }

    @Override
    public int compareTo(Cantidad otra) {
        return Integer.compare(unidades, otra.unidades);
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Cantidad && ((Cantidad) otro).unidades == unidades;
    }

    @Override
    public int hashCode() { return Integer.hashCode(unidades); }

    @Override
    public String toString() { return String.valueOf(unidades); }
}
