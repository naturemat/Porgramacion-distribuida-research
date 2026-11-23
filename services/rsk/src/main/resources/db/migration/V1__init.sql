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

CREATE TABLE senales_academicas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    estudiante_id uuid NOT NULL,
    nota_promedio numeric(6,2) NOT NULL,
    faltas integer NOT NULL,
    clases integer NOT NULL,
    entregas_tardias integer NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_senal UNIQUE (tenant_id, estudiante_id)
);

CREATE TABLE factores (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    caso_id uuid NOT NULL,
    nombre varchar(120) NOT NULL,
    valor numeric(10,2) NOT NULL,
    peso numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE puntajes (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    estudiante_id uuid NOT NULL,
    tipo varchar(30) NOT NULL,
    valor numeric(6,2) NOT NULL,
    calculado_en timestamptz NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE casos_riesgo (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    estudiante_id uuid NOT NULL,
    tipo varchar(30) NOT NULL,
    estado varchar(30) NOT NULL,
    puntaje numeric(6,2) NOT NULL,
    explicacion text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE revisiones (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    caso_id uuid NOT NULL,
    revisor_id uuid NOT NULL,
    decision varchar(30) NOT NULL,
    observacion text,
    created_at timestamptz NOT NULL
);

CREATE TABLE intervenciones (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    caso_id uuid NOT NULL,
    descripcion text NOT NULL,
    responsable_id uuid NOT NULL,
    fecha timestamptz NOT NULL,
    created_at timestamptz NOT NULL
);
