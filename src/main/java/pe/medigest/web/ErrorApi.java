package pe.medigest.web;

/** Error que se responde al navegador con un código HTTP y un mensaje claro. */
public class ErrorApi extends RuntimeException {
    private final int codigo;

    public ErrorApi(int codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public int getCodigo() { return codigo; }
}
