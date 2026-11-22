package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.excepcion.RecetaVencidaException;
import pe.medigest.farmacia.modelo.MotivoRechazo;
import pe.medigest.farmacia.tiempo.Reloj;
import pe.medigest.farmacia.tiempo.RelojSistema;

/** La receta debe estar dentro de su plazo de vigencia (según la política y el reloj que se usen). */
public class ReglaVigencia implements ReglaReceta {
    private final PoliticaVigencia politica;
    private final Reloj reloj;

    public ReglaVigencia(PoliticaVigencia politica, Reloj reloj) {
        this.politica = politica;
        this.reloj = reloj;
    }

    public ReglaVigencia() {
        this(new VigenciaEstandar(), new RelojSistema());
    }

    @Override
    public void verificar(Receta receta) {
        if (!politica.estaVigente(receta, reloj.hoy())) {
            throw new RecetaVencidaException("La receta " + receta.getCodigo() + " está vencida (emitida el "
                    + receta.getFechaEmision() + ", vale " + politica.diasDeVigencia(receta) + " días).");
        }
    }

    @Override
    public MotivoRechazo motivo() { return MotivoRechazo.RECETA_VENCIDA; }
}
