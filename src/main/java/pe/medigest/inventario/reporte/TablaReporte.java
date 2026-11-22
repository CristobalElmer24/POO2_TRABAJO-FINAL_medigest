package pe.medigest.inventario.reporte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado de un reporte: título, encabezados y filas de texto. Cada formateador la presenta a su manera. */
public class TablaReporte {

    private final String titulo;
    private final List<String> encabezados;
    private final List<List<String>> filas = new ArrayList<>();

    public TablaReporte(String titulo, List<String> encabezados) {
        this.titulo = titulo;
        this.encabezados = new ArrayList<>(encabezados);
    }

    public void agregarFila(String... celdas) {
        if (celdas.length != encabezados.size()) {
            throw new IllegalArgumentException("La fila tiene " + celdas.length + " celdas y la tabla " + encabezados.size() + " columnas.");
        }
        List<String> fila = new ArrayList<>();
        Collections.addAll(fila, celdas);
        filas.add(fila);
    }

    public String getTitulo() { return titulo; }

    public List<String> getEncabezados() { return Collections.unmodifiableList(encabezados); }

    public List<List<String>> getFilas() { return Collections.unmodifiableList(filas); }

    public int cantidadFilas() { return filas.size(); }
}
