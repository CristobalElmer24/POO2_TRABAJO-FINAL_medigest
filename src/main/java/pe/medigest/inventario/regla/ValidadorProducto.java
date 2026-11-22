package pe.medigest.inventario.regla;

import pe.medigest.inventario.Producto;
import pe.medigest.inventario.modelo.CodigoProducto;
import pe.medigest.comun.ReglaNegocioException;

/** Reglas de un producto antes de guardarlo (código válido, nombre, presentación y stock mínimo). */
public class ValidadorProducto {

    public void validar(Producto producto) {
        if (producto == null) {
            throw new ReglaNegocioException("El producto es obligatorio.");
        }
        CodigoProducto.de(producto.getCodigo());
        producto.validar();
        if (producto.getNombre().trim().length() > 80) {
            throw new ReglaNegocioException("El nombre del producto no puede pasar de 80 caracteres.");
        }
    }
}
