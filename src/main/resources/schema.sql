CREATE TABLE IF NOT EXISTS usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_usuario TEXT UNIQUE NOT NULL,
    hash_clave TEXT NOT NULL,
    rol TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS producto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT UNIQUE NOT NULL,
    nombre TEXT NOT NULL,
    presentacion TEXT NOT NULL,
    stock_minimo INTEGER NOT NULL CHECK (stock_minimo >= 0)
);

CREATE TABLE IF NOT EXISTS lote_almacen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    producto_id INTEGER NOT NULL,
    nro_lote TEXT NOT NULL,
    fecha_vencimiento TEXT NOT NULL,
    cantidad_disponible INTEGER NOT NULL CHECK (cantidad_disponible >= 0),
    UNIQUE (producto_id, nro_lote),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE IF NOT EXISTS receta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT UNIQUE NOT NULL,
    estado TEXT NOT NULL,
    fecha_emision TEXT NOT NULL,
    paciente_nombre TEXT NOT NULL,
    paciente_documento TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_receta (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    receta_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    FOREIGN KEY (receta_id) REFERENCES receta(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE IF NOT EXISTS ticket_farmacia (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    receta_id INTEGER NOT NULL,
    fecha_hora TEXT NOT NULL,
    usuario_id INTEGER NOT NULL,
    estado TEXT NOT NULL,
    FOREIGN KEY (receta_id) REFERENCES receta(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS detalle_ticket (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ticket_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    lote_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    FOREIGN KEY (ticket_id) REFERENCES ticket_farmacia(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (lote_id) REFERENCES lote_almacen(id)
);

CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    lote_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    tipo TEXT NOT NULL,
    motivo TEXT NOT NULL,
    fecha TEXT NOT NULL,
    FOREIGN KEY (lote_id) REFERENCES lote_almacen(id)
);

CREATE TABLE IF NOT EXISTS bitacora_auditoria (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario TEXT NOT NULL,
    accion TEXT NOT NULL,
    entidad TEXT NOT NULL,
    fecha_hora TEXT NOT NULL,
    resultado TEXT NOT NULL
);
