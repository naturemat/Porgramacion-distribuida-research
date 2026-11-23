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

CREATE TABLE cuestionarios (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    unidad_id uuid,
    periodo_id uuid NOT NULL,
    titulo varchar(200) NOT NULL,
    estado varchar(30) NOT NULL,
    intentos_maximos integer NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE preguntas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    cuestionario_id uuid NOT NULL,
    enunciado text NOT NULL,
    tipo varchar(30) NOT NULL,
    puntaje numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE opciones (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    pregunta_id uuid NOT NULL,
    texto text NOT NULL,
    correcta boolean NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE intentos (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    cuestionario_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    estado varchar(30) NOT NULL,
    iniciado_en timestamptz NOT NULL,
    enviado_en timestamptz,
    puntaje numeric(6,2),
    created_at timestamptz NOT NULL
);

CREATE TABLE respuestas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    intento_id uuid NOT NULL,
    pregunta_id uuid NOT NULL,
    opcion_id uuid,
    texto text,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_respuesta UNIQUE (tenant_id, intento_id, pregunta_id)
);
