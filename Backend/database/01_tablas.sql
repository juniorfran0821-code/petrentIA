CREATE ROLE petrent_app WITH LOGIN PASSWORD 'PetRent2026!';
GRANT ALL PRIVILEGES ON DATABASE petrent_db TO petrent_app;
GRANT ALL PRIVILEGES ON SCHEMA public TO petrent_app;

CREATE TABLE usuarios (
    id                   BIGSERIAL PRIMARY KEY,
    nombre               VARCHAR(100)  NOT NULL,
    correo               VARCHAR(150)  NOT NULL UNIQUE,
    password             VARCHAR(255)  NOT NULL,
    rol                  VARCHAR(20)   NOT NULL DEFAULT 'usuario',
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_rol CHECK (rol IN ('admin', 'usuario')),
    CONSTRAINT chk_correo_formato CHECK (correo ~ '^[^@\s]+@[^@\s]+\.[^@\s]+$')
);

CREATE TABLE perros (
    id                   BIGSERIAL PRIMARY KEY,
    id_propietario       BIGINT        NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    nombre               VARCHAR(60)   NOT NULL,
    raza                 VARCHAR(80)   NOT NULL,
    tamano               VARCHAR(20)   NOT NULL,
    precio               NUMERIC(6,2)  NOT NULL,
    descripcion          VARCHAR(500)  NOT NULL,
    etiquetas            VARCHAR(255),
    imagen               VARCHAR(255),
    disponible           BOOLEAN       NOT NULL DEFAULT TRUE,
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    calificacion         NUMERIC(2,1)  NOT NULL DEFAULT 0.0,
    resenas_totales      INT           NOT NULL DEFAULT 0,
    fecha_creacion       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_precio_positivo CHECK (precio > 0),
    CONSTRAINT chk_tamano CHECK (tamano IN ('Pequeño','Mediano','Grande')),
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 0 AND 5)
);

CREATE INDEX idx_perros_propietario ON perros(id_propietario);
CREATE INDEX idx_perros_nombre_lower ON perros(LOWER(nombre));
CREATE INDEX idx_perros_raza_lower ON perros(LOWER(raza));
CREATE INDEX idx_perros_activo_disponible ON perros(activo, disponible);

CREATE TABLE reservas (
    id                   BIGSERIAL PRIMARY KEY,
    id_usuario           BIGINT        NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    id_perro             BIGINT        NOT NULL REFERENCES perros(id)   ON DELETE RESTRICT,
    fecha_inicio         TIMESTAMP     NOT NULL,
    fecha_fin            TIMESTAMP     NOT NULL,
    horas                INT           NOT NULL,
    precio_total         NUMERIC(8,2)  NOT NULL,
    estado               VARCHAR(20)   NOT NULL DEFAULT 'CONFIRMADA',
    fecha_creacion       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_horas CHECK (horas BETWEEN 1 AND 8),
    CONSTRAINT chk_precio_total CHECK (precio_total > 0),
    CONSTRAINT chk_estado CHECK (estado IN ('CONFIRMADA','CANCELADA','COMPLETADA')),
    CONSTRAINT chk_fechas CHECK (fecha_fin > fecha_inicio)
);

CREATE INDEX idx_reservas_usuario ON reservas(id_usuario);
CREATE INDEX idx_reservas_perro ON reservas(id_perro);
CREATE INDEX idx_reservas_estado ON reservas(estado);

CREATE TABLE pagos (
    id                       BIGSERIAL PRIMARY KEY,
    id_reserva               BIGINT        NOT NULL UNIQUE REFERENCES reservas(id) ON DELETE RESTRICT,
    monto                     NUMERIC(8,2)  NOT NULL,
    metodo_pago               VARCHAR(20)   NOT NULL,
    estado_pago                VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    referencia_transaccion     VARCHAR(100),
    fecha_pago                 TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_monto_positivo CHECK (monto > 0),
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('TARJETA','EFECTIVO','TRANSFERENCIA','PAYPAL')),
    CONSTRAINT chk_estado_pago CHECK (estado_pago IN ('PENDIENTE','PAGADO','REEMBOLSADO'))
);

CREATE INDEX idx_pagos_estado ON pagos(estado_pago);

CREATE TABLE resenas (
    id                   BIGSERIAL PRIMARY KEY,
    id_reserva           BIGINT        NOT NULL UNIQUE REFERENCES reservas(id) ON DELETE RESTRICT,
    calificacion         INT           NOT NULL,
    comentario           VARCHAR(500),
    activo               BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_calificacion_resena CHECK (calificacion BETWEEN 1 AND 5)
);
