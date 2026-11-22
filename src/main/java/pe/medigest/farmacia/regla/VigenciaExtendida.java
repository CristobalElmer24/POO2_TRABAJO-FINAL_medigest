package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;

/** Vigencia más larga (por ejemplo, tratamientos crónicos): el triple de la normal. */
public class VigenciaExtendida implements PoliticaVigencia {
    @Override
    public int diasDeVigencia(Receta receta) {
        return Receta.DIAS_VIGENCIA * 3;
    }
}
