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

CREATE TABLE hechos_asistencia (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    fecha date NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE hechos_nota (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    componente varchar(120) NOT NULL,
    valor numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE hechos_entrega (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    puntual boolean NOT NULL,
    fecha timestamptz NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE hechos_riesgo (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    estudiante_id uuid NOT NULL,
    tipo varchar(30) NOT NULL,
    puntaje numeric(6,2) NOT NULL,
    fecha timestamptz NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE tableros (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nombre varchar(120) NOT NULL,
    tipo varchar(60) NOT NULL,
    filtros text,
    created_at timestamptz NOT NULL
);

CREATE TABLE consultas_guardadas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nombre varchar(120) NOT NULL,
    consulta text NOT NULL,
    created_at timestamptz NOT NULL
);
