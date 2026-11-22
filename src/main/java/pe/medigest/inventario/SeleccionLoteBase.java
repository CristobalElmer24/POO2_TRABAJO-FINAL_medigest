package pe.medigest.inventario;

import pe.medigest.comun.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Parte común de FEFO y FIFO: descartar vencidos, validar stock y recorrer los lotes en el orden elegido. */
public abstract class SeleccionLoteBase implements EstrategiaSeleccionLote {

    /** Orden en que se van a usar los lotes. Lo define cada estrategia. */
    protected abstract Comparator<LoteAlmacen> criterio();

    @Override
    public List<LoteAlmacen> seleccionar(List<LoteAlmacen> lotes, int cantidadRequerida) {
        if (cantidadRequerida <= 0) {
            throw new ReglaNegocioException("La cantidad requerida debe ser mayor a 0.");
        }

        List<LoteAlmacen> vigentes = new ArrayList<>();
        boolean hayVencidosConStock = false;
        int totalVigente = 0;

        for (LoteAlmacen lote : lotes) {
            if (lote.getCantidadDisponible() <= 0) {
                continue;
            }
            if (lote.estaVencido()) {
                hayVencidosConStock = true;
            } else {
                vigentes.add(lote);
                totalVigente += lote.getCantidadDisponible();
            }
        }

        if (totalVigente < cantidadRequerida) {
            if (vigentes.isEmpty() && hayVencidosConStock) {
                throw new LoteVencidoException("Todos los lotes con stock están vencidos. No se puede dispensar.");
            }
            String aviso = hayVencidosConStock ? " (se ignoraron los lotes vencidos)" : "";
            throw new StockInsuficienteException("Stock insuficiente en lotes vigentes: hay " + totalVigente
                    + " y se necesitan " + cantidadRequerida + aviso + ".");
        }

        vigentes.sort(criterio());

        List<LoteAlmacen> elegidos = new ArrayList<>();
        int acumulado = 0;
        for (LoteAlmacen lote : vigentes) {
            elegidos.add(lote);
            acumulado += lote.getCantidadDisponible();
            if (acumulado >= cantidadRequerida) {
                break;
            }
        }
        return elegidos;
    }
}
