CREATE TABLE outbox (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    topic varchar(128) NOT NULL,
    partition_key varchar(128) NOT NULL,
    payload text NOT NULL,
    created_at timestamptz NOT NULL,
    published_at timestamptz,
    attempts integer NOT NULL DEFAULT 0
);

CREATE INDEX idx_outbox_pending ON outbox (created_at) WHERE published_at IS NULL;

CREATE TABLE idempotency_record (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    key varchar(200) NOT NULL,
    response text NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_idempotency UNIQUE (tenant_id, key)
);

CREATE TABLE reportes (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    tipo varchar(60) NOT NULL,
    aula_id uuid,
    periodo_id uuid,
    estado varchar(30) NOT NULL,
    contenido text,
    generado_en timestamptz,
    created_at timestamptz NOT NULL
);

CREATE TABLE versiones_reporte (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    reporte_id uuid NOT NULL,
    version integer NOT NULL,
    contenido text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE plantillas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nombre varchar(120) NOT NULL,
    tipo varchar(60) NOT NULL,
    contenido text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE programaciones (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    tipo varchar(60) NOT NULL,
    cron varchar(120) NOT NULL,
    activa boolean NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE firmas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    reporte_id uuid NOT NULL,
    firmante_id uuid NOT NULL,
    hash varchar(128) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE notas_proyeccion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    valor numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_nota_proy UNIQUE (tenant_id, aula_id, estudiante_id, periodo_id)
);

CREATE TABLE asistencia_proyeccion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    faltas integer NOT NULL,
    clases integer NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_asistencia_proy UNIQUE (tenant_id, aula_id, estudiante_id)
);

CREATE TABLE casos_proyeccion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    estudiante_id uuid NOT NULL,
    tipo varchar(30) NOT NULL,
    puntaje numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE cierres_proyeccion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    cerrado_en timestamptz NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_cierre_proy UNIQUE (tenant_id, aula_id, periodo_id)
);
