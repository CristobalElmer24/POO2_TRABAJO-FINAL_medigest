package pe.medigest.farmacia;

import pe.medigest.farmacia.regla.CadenaValidacionReceta;
import pe.medigest.farmacia.regla.ResultadoValidacion;

/**
 * Reglas para poder dispensar: la receta existe, no fue dispensada, está vigente y tiene medicamentos.
 * Las reglas viven en el paquete farmacia.regla; este validador solo las aplica en cadena.
 */
public class ValidadorReceta {

    private final CadenaValidacionReceta cadena;

    public ValidadorReceta() {
        this(CadenaValidacionReceta.porDefecto());
    }

    public ValidadorReceta(CadenaValidacionReceta cadena) {
        this.cadena = cadena;
    }

    /** Lanza RecetaInvalidaException con el motivo si la receta no se puede dispensar. */
    public void validar(Receta receta) {
        cadena.validarOLanzar(receta);
    }

    /** Revisa todas las reglas y devuelve todos los problemas juntos (no lanza excepción). */
    public ResultadoValidacion evaluar(Receta receta) {
        return cadena.evaluar(receta);
    }
}
