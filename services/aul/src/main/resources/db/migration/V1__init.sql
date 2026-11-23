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

CREATE TABLE aulas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nombre varchar(200) NOT NULL,
    periodo_id uuid NOT NULL,
    estado varchar(30) NOT NULL,
    fecha_inicio timestamptz,
    fecha_fin timestamptz,
    created_at timestamptz NOT NULL
);

CREATE INDEX idx_aulas_tenant ON aulas (tenant_id);

CREATE TABLE matriculas_aula (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_matricula UNIQUE (tenant_id, aula_id, estudiante_id)
);

CREATE TABLE unidades (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    titulo varchar(200) NOT NULL,
    orden integer NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE anuncios (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    titulo varchar(200) NOT NULL,
    contenido text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE actividad_aula (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    hora timestamptz NOT NULL,
    contadores text NOT NULL,
    created_at timestamptz NOT NULL
);
