package pe.medigest.inventario.reporte;

import java.util.List;

/** CSV separado por comas; escapa comillas y comas dentro de las celdas. */
public class FormateadorCSV implements FormateadorReporte {

    @Override
    public String formatear(TablaReporte tabla) {
        StringBuilder sb = new StringBuilder();
        escribir(sb, tabla.getEncabezados());
        for (List<String> fila : tabla.getFilas()) {
            escribir(sb, fila);
        }
        return sb.toString();
    }

    private void escribir(StringBuilder sb, List<String> celdas) {
        for (int i = 0; i < celdas.size(); i++) {
            sb.append(escapar(celdas.get(i)));
            if (i < celdas.size() - 1) {
                sb.append(',');
            }
        }
        sb.append('\n');
    }

    private String escapar(String celda) {
        if (celda.contains(",") || celda.contains("\"") || celda.contains("\n")) {
            return "\"" + celda.replace("\"", "\"\"") + "\"";
        }
        return celda;
    }

    @Override
    public String getExtension() { return "csv"; }
}
