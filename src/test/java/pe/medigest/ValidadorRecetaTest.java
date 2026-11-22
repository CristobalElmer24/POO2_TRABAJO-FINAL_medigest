package pe.medigest;

import org.junit.jupiter.api.Test;
import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaInvalidaException;
import pe.medigest.farmacia.ValidadorReceta;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorRecetaTest {

    private final ValidadorReceta validador = new ValidadorReceta();
    private final LocalDate hoy = LocalDate.now();

    private Receta receta(String estado, LocalDate fecha, int cantidad) {
        Receta r = new Receta(1, "REC-X", estado, fecha, "Paciente de prueba", "12345678");
        r.agregarDetalle(new DetalleReceta(1, "Paracetamol", cantidad));
        return r;
    }

    @Test
    void unaRecetaEmitidaYVigenteEsValida() {
        assertDoesNotThrow(() -> validador.validar(receta(Receta.EMITIDA, hoy.minusDays(2), 5)));
    }

    @Test
    void unaRecetaInexistenteSeRechaza() {
        assertThrows(RecetaInvalidaException.class, () -> validador.validar(null));
    }

    @Test
    void unaRecetaYaDispensadaSeRechaza() {
        assertThrows(RecetaInvalidaException.class,
                () -> validador.validar(receta(Receta.DISPENSADA, hoy, 5)));
    }

    @Test
    void unaRecetaVencidaSeRechaza() {
        assertThrows(RecetaInvalidaException.class,
                () -> validador.validar(receta(Receta.EMITIDA, hoy.minusDays(45), 5)));
    }

    @Test
    void elDiaTreintaTodaviaEsVigente() {
        assertTrue(receta(Receta.EMITIDA, hoy.minusDays(Receta.DIAS_VIGENCIA), 5).estaVigente());
    }

    @Test
    void unaRecetaSinMedicamentosOConCantidadCeroSeRechaza() {
        Receta vacia = new Receta(2, "REC-V", Receta.EMITIDA, hoy, "Paciente", "1");
        assertThrows(RecetaInvalidaException.class, () -> validador.validar(vacia));
        assertThrows(RecetaInvalidaException.class,
                () -> validador.validar(receta(Receta.EMITIDA, hoy, 0)));
    }
}
