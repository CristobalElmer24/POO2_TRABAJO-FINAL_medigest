package pe.medigest.farmacia.regla;

import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.modelo.MotivoRechazo;

/** Una condición que una receta debe cumplir para poder dispensarse. */
public interface ReglaReceta {

    /** Lanza una RecetaInvalidaException con el mensaje para el usuario si no se cumple. */
    void verificar(Receta receta) throws RecetaInvalidaException;

    /** Por qué se rechaza cuando esta regla falla. */
    MotivoRechazo motivo();
}
