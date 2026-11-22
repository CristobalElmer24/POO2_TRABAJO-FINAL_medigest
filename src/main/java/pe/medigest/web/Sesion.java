package pe.medigest.web;

import pe.medigest.comun.Usuario;
import pe.medigest.farmacia.DispensacionController;
import pe.medigest.inventario.InventarioController;
import pe.medigest.inventario.ServicioInventario;

/** Lo que el servidor recuerda de cada persona conectada: quién es y sus controladores. */
public class Sesion {

    private final Usuario usuario;
    private final InventarioController inventario;
    private final DispensacionController farmacia;

    public Sesion(Usuario usuario, ServicioInventario servicio) {
        this.usuario = usuario;
        this.inventario = new InventarioController(servicio, usuario);
        this.farmacia = new DispensacionController(servicio, usuario);
    }

    public Usuario getUsuario() { return usuario; }

    public InventarioController getInventario() { return inventario; }

    public DispensacionController getFarmacia() { return farmacia; }
}
