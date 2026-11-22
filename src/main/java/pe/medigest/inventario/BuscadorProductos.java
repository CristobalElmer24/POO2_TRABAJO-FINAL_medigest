package pe.medigest.inventario;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Busca productos por parte del código, nombre o presentación, sin importar mayúsculas ni tildes. */
public class BuscadorProductos {

    public List<Producto> buscar(List<Producto> productos, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return new ArrayList<>(productos);
        }
        String buscado = normalizar(texto);
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (normalizar(p.getCodigo()).contains(buscado) || normalizar(p.getNombre()).contains(buscado)
                    || normalizar(p.getPresentacion()).contains(buscado)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
