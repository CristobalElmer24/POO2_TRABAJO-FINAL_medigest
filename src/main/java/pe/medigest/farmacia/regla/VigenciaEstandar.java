package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;

/** Vigencia normal: {@link Receta#DIAS_VIGENCIA} días desde la emisión. */
public class VigenciaEstandar implements PoliticaVigencia {
    @Override
    public int diasDeVigencia(Receta receta) {
        return Receta.DIAS_VIGENCIA;
    }
}
