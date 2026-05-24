-- ============================================================
-- init.sql - Script de inicialización de base de datos
-- Generado desde diagrama ER
-- ============================================================

-- ========================
-- Tabla: Rol
-- ========================
CREATE TABLE rol (
    id_rol      BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL
);

-- ========================
-- Tabla: Usuario
-- ========================
CREATE TABLE usuario (
    id_usuario  BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,
    id_rol      BIGINT       NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

-- ========================
-- Tabla: Zona
-- ========================
CREATE TABLE zona (
    id_zona     BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    tipo        VARCHAR(100) NOT NULL,
    activa      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ========================
-- Tabla: Turno
-- ========================
CREATE TABLE turno (
    id_turno              BIGSERIAL PRIMARY KEY,
    fecha                 DATE         NOT NULL,
    hora_inicio           TIME         NOT NULL,
    hora_fin              TIME         NOT NULL,
    estado                VARCHAR(50)  NOT NULL,
    limpieza_calificacion INTEGER NOT NULL,
    id_docente            BIGINT       NOT NULL,
    id_zona               BIGINT       NOT NULL,
    CONSTRAINT fk_turno_docente FOREIGN KEY (id_docente) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_turno_zona    FOREIGN KEY (id_zona)    REFERENCES zona(id_zona)
);

-- ========================
-- Tabla: Checkpoint
-- ========================
CREATE TABLE checkpoint (
    id_checkpoint   BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    codigo_qr       VARCHAR(255) NOT NULL,
    id_zona         BIGINT       NOT NULL,
    CONSTRAINT fk_checkpoint_zona FOREIGN KEY (id_zona) REFERENCES zona(id_zona)
);

-- ========================
-- Tabla: MetricasDocente
-- ========================
CREATE TABLE metricas_docente (
    id_metrica              BIGSERIAL PRIMARY KEY,
    periodo                 VARCHAR(50)      NOT NULL,
    puntualidad_porcentaje  DOUBLE PRECISION NOT NULL DEFAULT 0,
    recorridos_promedio     DOUBLE PRECISION NOT NULL DEFAULT 0,
    incidentes_reportados   INTEGER          NOT NULL DEFAULT 0,
    puntos_totales          INTEGER          NOT NULL DEFAULT 0,
    id_docente              BIGINT           NOT NULL,
    CONSTRAINT fk_metricas_docente FOREIGN KEY (id_docente) REFERENCES usuario(id_usuario)
);

-- ========================
-- Tabla: Notificacion
-- ========================
CREATE TABLE notificacion (
    id_notificacion BIGSERIAL PRIMARY KEY,
    mensaje         TEXT         NOT NULL,
    tipo            VARCHAR(100) NOT NULL,
    fecha_envio     TIMESTAMP    NOT NULL DEFAULT NOW(),
    leida           BOOLEAN      NOT NULL DEFAULT FALSE,
    id_usuario      BIGINT       NOT NULL,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- ========================
-- Tabla: Reasignacion
-- ========================
CREATE TABLE reasignacion (
    id_reasignacion     BIGSERIAL PRIMARY KEY,
    motivo              TEXT         NOT NULL,
    fecha_propuesta     TIMESTAMP    NOT NULL,
    fecha_respuesta     TIMESTAMP,
    estado              VARCHAR(50)  NOT NULL,
    id_turno            BIGINT       NOT NULL,
    id_docente_original BIGINT       NOT NULL,
    id_docente_propuesto BIGINT      NOT NULL,
    CONSTRAINT fk_reasignacion_turno             FOREIGN KEY (id_turno)             REFERENCES turno(id_turno),
    CONSTRAINT fk_reasignacion_docente_original  FOREIGN KEY (id_docente_original)  REFERENCES usuario(id_usuario),
    CONSTRAINT fk_reasignacion_docente_propuesto FOREIGN KEY (id_docente_propuesto) REFERENCES usuario(id_usuario)
);

-- ========================
-- Tabla: CheckInTurno
-- ========================
CREATE TABLE check_in_turno (
    id_checkin  BIGSERIAL PRIMARY KEY,
    fecha_hora  TIMESTAMP    NOT NULL DEFAULT NOW(),
    metodo      VARCHAR(100) NOT NULL,
    id_turno    BIGINT       NOT NULL,
    CONSTRAINT fk_checkin_turno FOREIGN KEY (id_turno) REFERENCES turno(id_turno)
);

-- ========================
-- Tabla: Recorrido
-- ========================
CREATE TABLE recorrido (
    id_recorrido    BIGSERIAL PRIMARY KEY,
    fecha_hora      TIMESTAMP NOT NULL DEFAULT NOW(),
    id_turno        BIGINT    NOT NULL,
    id_checkpoint   BIGINT    NOT NULL,
    CONSTRAINT fk_recorrido_turno      FOREIGN KEY (id_turno)      REFERENCES turno(id_turno),
    CONSTRAINT fk_recorrido_checkpoint FOREIGN KEY (id_checkpoint) REFERENCES checkpoint(id_checkpoint)
);

-- ========================
-- Tabla: TipoIncidente
-- ========================
CREATE TABLE tipo_incidente (
    id_tipo BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(150) NOT NULL
);

-- ========================
-- Tabla: Departamento
-- ========================
CREATE TABLE departamento (
    id_departamento BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL
);

-- ========================
-- Tabla: Severidad
-- ========================
CREATE TABLE severidad (
    id_severidad    BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(50)  NOT NULL,
    descripcion     TEXT NOT NULL
);

-- ========================
-- Tabla: Incidente
-- ========================
CREATE TABLE incidente (
    id_incidente    BIGSERIAL PRIMARY KEY,
    fecha_hora      TIMESTAMP    NOT NULL DEFAULT NOW(),
    descripcion     TEXT         NOT NULL,
    id_turno        BIGINT       NOT NULL,
    id_zona         BIGINT       NOT NULL,
    id_tipo         BIGINT       NOT NULL,
    id_severidad    BIGINT       NOT NULL,
    CONSTRAINT fk_incidente_turno     FOREIGN KEY (id_turno)     REFERENCES turno(id_turno),
    CONSTRAINT fk_incidente_zona      FOREIGN KEY (id_zona)      REFERENCES zona(id_zona),
    CONSTRAINT fk_incidente_tipo      FOREIGN KEY (id_tipo)      REFERENCES tipo_incidente(id_tipo),
    CONSTRAINT fk_incidente_severidad FOREIGN KEY (id_severidad) REFERENCES severidad(id_severidad)
);

INSERT INTO rol (id_rol, nombre)
VALUES
    (1, 'ADMINISTRADOR'),
    (2, 'COORDINADOR'),
    (3, 'PROFESOR')
ON CONFLICT (id_rol) DO NOTHING;

-- Ajusta la secuencia de rol cuando se insertan ids manuales
SELECT setval('rol_id_rol_seq', (SELECT COALESCE(MAX(id_rol), 1) FROM rol), true);

-- admin123
INSERT INTO usuario (nombre, email, password, estado, id_rol)
VALUES ('Administrador', 'admin@ejemplo.com', '$2b$10$iI22Xfy4CIirRnjOGaEhuuY21S8kcEjGkaan79MOa3DPjF2HgmW7C', true, 1)
ON CONFLICT (email) DO NOTHING;

-- coordinador123
INSERT INTO usuario (nombre, email, password, estado, id_rol)
VALUES ('Coordinador Prueba', 'coordinador@ejemplo.com', '$2b$10$6MHyhbDZ3mqtq0oUH1bZge0ymrPWvtfOIgGAH5YXgngO/CfJXPBhO', true, 2)
ON CONFLICT (email) DO NOTHING;

-- docente123
INSERT INTO usuario (nombre, email, password, estado, id_rol)
VALUES ('Profesor Prueba', 'profesor@ejemplo.com', '$2b$10$RjB4pGlRdtXOJcqXeYBNuOJt37LsEWFxPJIosOZ9TwcbbTHya0.GK', true, 3)
ON CONFLICT (email) DO NOTHING;

-- ========================
-- Catálogo: Zonas
-- (ids alineados con el frontend: features/profesor/pages/reporte/reporte.ts)
-- ========================
INSERT INTO zona (id_zona, nombre, descripcion, tipo, activa)
VALUES
    (1, 'Bloque A',      'Ingreso principal y pasillos administrativos',     'BLOQUE', true),
    (2, 'Bloque B',      'Aulas de ciencias y laboratorios',                 'BLOQUE', true),
    (3, 'Patio Central', 'Zona de recreo y circulación de estudiantes',      'PATIO',  true),
    (4, 'Bloque C',      'Biblioteca, sala de docentes y apoyo académico',   'BLOQUE', true)
ON CONFLICT (id_zona) DO NOTHING;

SELECT setval('zona_id_zona_seq', (SELECT COALESCE(MAX(id_zona), 1) FROM zona), true);

-- ========================
-- Catálogo: Tipos de Incidente
-- (ids alineados con el frontend)
-- ========================
INSERT INTO tipo_incidente (id_tipo, nombre)
VALUES
    (1, 'Seguridad Física'),
    (2, 'Convivencia'),
    (3, 'Uso del Espacio'),
    (4, 'Observación Social')
ON CONFLICT (id_tipo) DO NOTHING;

SELECT setval('tipo_incidente_id_tipo_seq', (SELECT COALESCE(MAX(id_tipo), 1) FROM tipo_incidente), true);

-- ========================
-- Catálogo: Severidades
-- (ids alineados con el frontend: S1=1, S2=2, S3=3)
-- ========================
INSERT INTO severidad (id_severidad, codigo, descripcion)
VALUES
    (1, 'S1', 'Leve - Situación menor'),
    (2, 'S2', 'Seguimiento - Requiere atención'),
    (3, 'S3', 'Urgente - Atención inmediata')
ON CONFLICT (id_severidad) DO NOTHING;

SELECT setval('severidad_id_severidad_seq', (SELECT COALESCE(MAX(id_severidad), 1) FROM severidad), true);

-- ========================
-- Catálogo: Departamentos
-- (ids alineados con el frontend: 1=Enfermería, 2=Aseo, 3=Psicología, 4=Vida Comunitaria)
-- ========================
INSERT INTO departamento (id_departamento, nombre)
VALUES
    (1, 'Enfermería'),
    (2, 'Aseo'),
    (3, 'Psicología'),
    (4, 'Vida Comunitaria')
ON CONFLICT (id_departamento) DO NOTHING;

SELECT setval('departamento_id_departamento_seq', (SELECT COALESCE(MAX(id_departamento), 1) FROM departamento), true);