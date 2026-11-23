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

CREATE TABLE umbrales (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nombre varchar(120) NOT NULL,
    porcentaje numeric(6,2) NOT NULL,
    vigente_desde timestamptz NOT NULL,
    version integer NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE cumplimiento (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    faltas integer NOT NULL,
    clases integer NOT NULL,
    porcentaje numeric(6,2) NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_cumplimiento UNIQUE (tenant_id, aula_id, estudiante_id)
);

CREATE TABLE alertas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    tipo varchar(60) NOT NULL,
    mensaje text NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);
