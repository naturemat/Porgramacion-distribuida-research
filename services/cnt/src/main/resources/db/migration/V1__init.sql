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

CREATE TABLE contenidos (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    unidad_id uuid,
    titulo varchar(200) NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE versiones_contenido (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    contenido_id uuid NOT NULL,
    numero integer NOT NULL,
    cuerpo text NOT NULL,
    archivo_id uuid,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_version UNIQUE (tenant_id, contenido_id, numero)
);

CREATE TABLE referencias_archivo (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    contenido_id uuid NOT NULL,
    archivo_id uuid NOT NULL,
    nombre varchar(255) NOT NULL,
    tipo_mime varchar(120) NOT NULL,
    tamano bigint NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_archivo UNIQUE (tenant_id, archivo_id)
);
