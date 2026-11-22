package pe.medigest.inventario.modelo;

/** Clasificación terapéutica simple de un producto del catálogo. */
public enum CategoriaProducto {
    ANALGESICO("Analgésico"),
    ANTIBIOTICO("Antibiótico"),
    ANTIHISTAMINICO("Antihistamínico"),
    ANTIINFLAMATORIO("Antiinflamatorio"),
    OTROS("Otros");

    private final String etiqueta;

    CategoriaProducto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() { return etiqueta; }

    /** Adivina la categoría por el nombre del producto (para datos de prueba); si no sabe, devuelve OTROS. */
    public static CategoriaProducto deducirDe(String nombreProducto) {
        if (nombreProducto == null) {
            return OTROS;
        }
        String n = nombreProducto.toLowerCase();
        if (n.contains("paracetamol")) return ANALGESICO;
        if (n.contains("amoxicilina")) return ANTIBIOTICO;
        if (n.contains("loratadina")) return ANTIHISTAMINICO;
        if (n.contains("ibuprofeno") || n.contains("naproxeno")) return ANTIINFLAMATORIO;
        return OTROS;
    }
}
