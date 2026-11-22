package pe.medigest.inventario.reporte;

import java.util.List;

/** Tabla de texto con columnas alineadas, para mostrar en pantalla o imprimir. */
public class FormateadorTexto implements FormateadorReporte {

    @Override
    public String formatear(TablaReporte tabla) {
        List<String> enc = tabla.getEncabezados();
        int[] ancho = new int[enc.size()];
        for (int i = 0; i < enc.size(); i++) {
            ancho[i] = enc.get(i).length();
        }
        for (List<String> fila : tabla.getFilas()) {
            for (int i = 0; i < fila.size(); i++) {
                ancho[i] = Math.max(ancho[i], fila.get(i).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tabla.getTitulo()).append('\n');
        agregarFila(sb, enc, ancho);
        int total = 0;
        for (int a : ancho) {
            total += a + 2;
        }
        sb.append("-".repeat(Math.max(total - 2, 1))).append('\n');
        for (List<String> fila : tabla.getFilas()) {
            agregarFila(sb, fila, ancho);
        }
        return sb.toString();
    }

    private void agregarFila(StringBuilder sb, List<String> fila, int[] ancho) {
        for (int i = 0; i < fila.size(); i++) {
            sb.append(String.format("%-" + ancho[i] + "s", fila.get(i)));
            if (i < fila.size() - 1) {
                sb.append("  ");
            }
        }
        sb.append('\n');
    }

    @Override
    public String getExtension() { return "txt"; }
}
