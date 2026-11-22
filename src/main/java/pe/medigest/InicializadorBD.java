package pe.medigest;

import pe.medigest.comun.AccesoDatosException;
import pe.medigest.comun.ConexionBD;
import pe.medigest.comun.Usuario;
import pe.medigest.comun.UsuarioDAO;
import pe.medigest.comun.UsuarioDAOJdbc;
import pe.medigest.farmacia.DetalleReceta;
import pe.medigest.farmacia.Receta;
import pe.medigest.farmacia.RecetaDAO;
import pe.medigest.farmacia.RecetaDAOJdbc;
import pe.medigest.inventario.LoteAlmacen;
import pe.medigest.inventario.LoteDAO;
import pe.medigest.inventario.LoteDAOJdbc;
import pe.medigest.inventario.Producto;
import pe.medigest.inventario.ProductoDAO;
import pe.medigest.inventario.ProductoDAOJdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/** Crea las tablas y carga datos FICTICIOS la primera vez que se ejecuta el programa (RNF04). */
public class InicializadorBD {

    private InicializadorBD() { }

    public static void preparar() {
        crearTablas();
        if (new UsuarioDAOJdbc().contar() == 0) {
            cargarDatosDePrueba();
        }
    }

    private static void crearTablas() {
        try (InputStream in = InicializadorBD.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró schema.sql en resources.");
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            synchronized (ConexionBD.CANDADO) {
                for (String sentencia : script.split(";")) {
                    if (!sentencia.trim().isEmpty()) {
                        try (Statement st = ConexionBD.getInstancia().getConexion().createStatement()) {
                            st.execute(sentencia);
                        }
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new AccesoDatosException("No se pudieron crear las tablas.", e);
        }
    }

    private static void cargarDatosDePrueba() {
        ConexionBD.getInstancia().enTransaccionVoid(() -> {
            LocalDate hoy = LocalDate.now();

            // Usuarios (contraseña igual al nombre de usuario, solo para el prototipo)
            UsuarioDAO usuarios = new UsuarioDAOJdbc();
            usuarios.insertar("admin", Usuario.generarHash("admin"), Usuario.ADMINISTRADOR);
            usuarios.insertar("farmacia", Usuario.generarHash("farmacia"), Usuario.FARMACIA);
            usuarios.insertar("inventario", Usuario.generarHash("inventario"), Usuario.INVENTARIO);

            // Productos
            ProductoDAO productos = new ProductoDAOJdbc();
            Producto paracetamol = nuevoProducto(productos, "P001", "Paracetamol", "500 mg", 20);
            Producto ibuprofeno = nuevoProducto(productos, "P002", "Ibuprofeno", "400 mg", 15);
            Producto amoxicilina = nuevoProducto(productos, "P003", "Amoxicilina", "500 mg", 30);
            Producto loratadina = nuevoProducto(productos, "P004", "Loratadina", "10 mg", 10);

            // Lotes: hay de todo para poder probar las reglas
            LoteDAO lotes = new LoteDAOJdbc();
            lotes.insertar(new LoteAlmacen(0, paracetamol.getId(), "PARA-A", hoy.plusDays(400), 100));
            lotes.insertar(new LoteAlmacen(0, paracetamol.getId(), "PARA-B", hoy.minusDays(300), 50));  // vencido
            lotes.insertar(new LoteAlmacen(0, paracetamol.getId(), "PARA-C", hoy.plusDays(20), 40));    // por vencer
            lotes.insertar(new LoteAlmacen(0, ibuprofeno.getId(), "IBU-A", hoy.plusDays(240), 200));
            lotes.insertar(new LoteAlmacen(0, amoxicilina.getId(), "AMOX-A", hoy.plusDays(120), 25));   // bajo el mínimo
            lotes.insertar(new LoteAlmacen(0, loratadina.getId(), "LORA-A", hoy.minusDays(30), 30));    // solo vencido

            // Recetas
            RecetaDAO recetas = new RecetaDAOJdbc();

            // REC-001: se puede dispensar sin problemas
            Receta r1 = nuevaReceta("REC-001", Receta.EMITIDA, hoy.minusDays(2), "María Quispe Huamán", "45871236");
            r1.agregarDetalle(new DetalleReceta(paracetamol.getId(), "Paracetamol", 10));
            r1.agregarDetalle(new DetalleReceta(ibuprofeno.getId(), "Ibuprofeno", 5));
            recetas.insertar(r1);

            // REC-002: ya fue dispensada
            Receta r2 = nuevaReceta("REC-002", Receta.DISPENSADA, hoy.minusDays(10), "Luis Ramos Ccori", "70234518");
            r2.agregarDetalle(new DetalleReceta(ibuprofeno.getId(), "Ibuprofeno", 10));
            recetas.insertar(r2);

            // REC-003: receta vencida (más de 30 días)
            Receta r3 = nuevaReceta("REC-003", Receta.EMITIDA, hoy.minusDays(45), "Ana Torres Salas", "41562897");
            r3.agregarDetalle(new DetalleReceta(paracetamol.getId(), "Paracetamol", 5));
            recetas.insertar(r3);

            // REC-004: el paracetamol alcanza pero la amoxicilina no -> debe fallar y no descontar NADA (rollback)
            Receta r4 = nuevaReceta("REC-004", Receta.EMITIDA, hoy.minusDays(1), "Carlos Mendoza Vega", "46012785");
            r4.agregarDetalle(new DetalleReceta(paracetamol.getId(), "Paracetamol", 5));
            r4.agregarDetalle(new DetalleReceta(amoxicilina.getId(), "Amoxicilina", 40));
            recetas.insertar(r4);

            // REC-005: solo hay lotes vencidos
            Receta r5 = nuevaReceta("REC-005", Receta.EMITIDA, hoy.minusDays(3), "Rosa Flores Díaz", "43210987");
            r5.agregarDetalle(new DetalleReceta(loratadina.getId(), "Loratadina", 5));
            recetas.insertar(r5);

            // REC-006: 60 de paracetamol -> FEFO usa primero PARA-C (vence antes) y completa con PARA-A
            Receta r6 = nuevaReceta("REC-006", Receta.EMITIDA, hoy, "Jorge Salazar Paredes", "44556677");
            r6.agregarDetalle(new DetalleReceta(paracetamol.getId(), "Paracetamol", 60));
            recetas.insertar(r6);
        });
    }

    private static Producto nuevoProducto(ProductoDAO dao, String codigo, String nombre, String presentacion, int minimo) {
        Producto p = new Producto(codigo, nombre, presentacion, minimo);
        dao.insertar(p);
        return p;
    }

    private static Receta nuevaReceta(String codigo, String estado, LocalDate fecha, String paciente, String documento) {
        return new Receta(0, codigo, estado, fecha, paciente, documento);
    }
}
