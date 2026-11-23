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

CREATE TABLE componentes_evaluacion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    nombre varchar(120) NOT NULL,
    peso numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_componente UNIQUE (tenant_id, aula_id, periodo_id, nombre)
);

CREATE TABLE notas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    componente_id uuid NOT NULL,
    valor numeric(6,2) NOT NULL,
    origen varchar(30) NOT NULL,
    origen_id uuid,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_nota_origen UNIQUE (tenant_id, aula_id, estudiante_id, componente_id, origen, origen_id)
);

CREATE TABLE versiones_nota (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    nota_id uuid NOT NULL,
    valor_anterior numeric(6,2),
    valor_nuevo numeric(6,2) NOT NULL,
    motivo text NOT NULL,
    creada_por uuid,
    created_at timestamptz NOT NULL
);

CREATE TABLE cierres_periodo (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    estado varchar(30) NOT NULL,
    cerrado_en timestamptz,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_cierre UNIQUE (tenant_id, aula_id, periodo_id)
);

CREATE TABLE promedios (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    valor numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_promedio UNIQUE (tenant_id, aula_id, estudiante_id, periodo_id)
);
