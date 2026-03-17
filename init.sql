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