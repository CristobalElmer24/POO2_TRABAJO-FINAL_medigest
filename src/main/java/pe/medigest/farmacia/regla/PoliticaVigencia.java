package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;

import java.time.LocalDate;

/** Strategy: define cuántos días vale una receta. */
public interface PoliticaVigencia {

    int diasDeVigencia(Receta receta);

    default boolean estaVigente(Receta receta, LocalDate hoy) {
        return !receta.getFechaEmision().plusDays(diasDeVigencia(receta)).isBefore(hoy);
    }
}
