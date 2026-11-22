package pe.medigest.farmacia.modelo;

import pe.medigest.farmacia.excepcion.DocumentoInvalidoException;

import java.util.Objects;

/** Documento de identidad ya validado: un DNI de 8 dígitos, un carnet de extranjería, etc. */
public final class DocumentoPaciente {

    private final TipoDocumento tipo;
    private final String numero;

    private DocumentoPaciente(TipoDocumento tipo, String numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    public static DocumentoPaciente de(TipoDocumento tipo, String numero) {
        if (tipo == null) {
            throw new DocumentoInvalidoException("El tipo de documento es obligatorio.");
        }
        String limpio = numero == null ? "" : numero.trim().toUpperCase();
        if (!tipo.aceptaNumero(limpio)) {
            throw new DocumentoInvalidoException("El número '" + (numero == null ? "" : numero.trim())
                    + "' no es válido para " + tipo.getAbreviatura() + ".");
        }
        return new DocumentoPaciente(tipo, limpio);
    }

    public static DocumentoPaciente dni(String numero) {
        return de(TipoDocumento.DNI, numero);
    }

    public TipoDocumento getTipo() { return tipo; }

    public String getNumero() { return numero; }

    @Override
    public boolean equals(Object otro) {
        if (!(otro instanceof DocumentoPaciente)) {
            return false;
        }
        DocumentoPaciente d = (DocumentoPaciente) otro;
        return d.tipo == tipo && d.numero.equals(numero);
    }

    @Override
    public int hashCode() { return Objects.hash(tipo, numero); }

    @Override
    public String toString() { return tipo.getAbreviatura() + " " + numero; }
}
