package pe.medigest.inventario.regla;

import pe.medigest.inventario.modelo.NivelStock;

/**
 * Convierte "unidades utilizables vs. stock mínimo" en un {@link NivelStock}.
 * Críticas: hasta la mitad del mínimo. Bajo: hasta el mínimo. Normal: hasta el doble. Alto: más del doble.
 */
public class ClasificadorNivelStock {

    public NivelStock clasificar(int unidadesUtilizables, int stockMinimo) {
        if (unidadesUtilizables <= 0) {
            return NivelStock.SIN_STOCK;
        }
        if (unidadesUtilizables * 2 <= stockMinimo) {
            return NivelStock.CRITICO;
        }
        if (unidadesUtilizables <= stockMinimo) {
            return NivelStock.BAJO;
        }
        if (unidadesUtilizables <= stockMinimo * 2) {
            return NivelStock.NORMAL;
        }
        return NivelStock.ALTO;
    }
}
