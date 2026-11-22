package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.tiempo.Reloj;
import pe.medigest.farmacia.tiempo.RelojSistema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cadena de reglas que se aplican en orden a una receta (estilo Chain of Responsibility).
 * validarOLanzar corta en la primera que falla; evaluar las prueba todas y junta los problemas.
 */
public class CadenaValidacionReceta {

    private final List<ReglaReceta> reglas;

    public CadenaValidacionReceta(List<ReglaReceta> reglas) {
        this.reglas = new ArrayList<>(reglas);
    }

    /** Las reglas que exige el negocio: existe, no dispensada, vigente, con medicamentos y cantidades > 0. */
    public static CadenaValidacionReceta porDefecto(Reloj reloj) {
        return new CadenaValidacionReceta(Arrays.asList(
                new ReglaRecetaExiste(), new ReglaNoDispensada(),
                new ReglaVigencia(new VigenciaEstandar(), reloj),
                new ReglaConMedicamentos(), new ReglaCantidadesPositivas()));
    }

    public static CadenaValidacionReceta porDefecto() {
        return porDefecto(new RelojSistema());
    }

    /** Igual que la normal, más la regla de medicamentos repetidos. */
    public static CadenaValidacionReceta estricta(Reloj reloj) {
        CadenaValidacionReceta base = porDefecto(reloj);
        List<ReglaReceta> todas = new ArrayList<>(base.reglas);
        todas.add(new ReglaSinRepetidos());
        return new CadenaValidacionReceta(todas);
    }

    public void validarOLanzar(Receta receta) {
        for (ReglaReceta regla : reglas) {
            regla.verificar(receta);
        }
    }

    public ResultadoValidacion evaluar(Receta receta) {
        ResultadoValidacion resultado = new ResultadoValidacion();
        for (ReglaReceta regla : reglas) {
            try {
                regla.verificar(receta);
            } catch (RecetaInvalidaException e) {
                resultado.agregar(regla.motivo(), e.getMessage());
                if (receta == null) {
                    break; // sin receta no tiene sentido seguir con las demás reglas
                }
            }
        }
        return resultado;
    }

    public int cantidadReglas() { return reglas.size(); }
}
