package pe.medigest.farmacia.reporte;

import java.time.LocalDate;

/** Lo que pasó en farmacia un día: tickets dispensados, anulados y unidades entregadas. */
public class ResumenDiario {
    private final LocalDate fecha;
    private int dispensados;
    private int anulados;
    private int unidades;

    public ResumenDiario(LocalDate fecha) {
        this.fecha = fecha;
    }

    void sumarDispensado(int unidadesEntregadas) {
        dispensados++;
        unidades += unidadesEntregadas;
    }

    void sumarAnulado() { anulados++; }

    public LocalDate getFecha() { return fecha; }

    public int getDispensados() { return dispensados; }

    public int getAnulados() { return anulados; }

    public int getUnidades() { return unidades; }
}
