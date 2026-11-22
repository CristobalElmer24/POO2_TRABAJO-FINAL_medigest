package pe.medigest.farmacia.modelo;

import java.util.regex.Pattern;

/** Tipo de documento de identidad del paciente, con su formato. */
public enum TipoDocumento {
    DNI("DNI", "^\\d{8}$"),
    CARNET_EXTRANJERIA("CE", "^[A-Z0-9]{9,12}$"),
    PASAPORTE("PAS", "^[A-Z0-9]{6,12}$");

    private final String abreviatura;
    private final Pattern formato;

    TipoDocumento(String abreviatura, String regex) {
        this.abreviatura = abreviatura;
        this.formato = Pattern.compile(regex);
    }

    public String getAbreviatura() { return abreviatura; }

    public boolean aceptaNumero(String numero) {
        return numero != null && formato.matcher(numero).matches();
    }
}
