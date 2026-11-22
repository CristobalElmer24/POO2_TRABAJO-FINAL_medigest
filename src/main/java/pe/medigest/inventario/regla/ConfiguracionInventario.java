package pe.medigest.inventario.regla;

import pe.medigest.comun.ReglaNegocioException;

/** Parámetros del inventario que antes estaban sueltos en el código (días de aviso, días de reposición, etc.). */
public class ConfiguracionInventario {

    private final int diasAlertaVencimiento;
    private final int diasCoberturaDeseada;
    private final int ventanaConsumoDias;

    public ConfiguracionInventario(int diasAlertaVencimiento, int diasCoberturaDeseada, int ventanaConsumoDias) {
        if (diasAlertaVencimiento < 1 || diasCoberturaDeseada < 1 || ventanaConsumoDias < 1) {
            throw new ReglaNegocioException("Los días de la configuración deben ser mayores a 0.");
        }
        this.diasAlertaVencimiento = diasAlertaVencimiento;
        this.diasCoberturaDeseada = diasCoberturaDeseada;
        this.ventanaConsumoDias = ventanaConsumoDias;
    }

    /** 30 días de aviso de vencimiento, 30 días de cobertura y 30 días de historial de consumo. */
    public static ConfiguracionInventario porDefecto() {
        return new ConfiguracionInventario(30, 30, 30);
    }

    public int getDiasAlertaVencimiento() { return diasAlertaVencimiento; }

    public int getDiasCoberturaDeseada() { return diasCoberturaDeseada; }

    public int getVentanaConsumoDias() { return ventanaConsumoDias; }
}
