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

CREATE TABLE tareas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    unidad_id uuid,
    periodo_id uuid NOT NULL,
    titulo varchar(200) NOT NULL,
    descripcion text,
    fecha_limite timestamptz NOT NULL,
    puntaje_maximo numeric(6,2) NOT NULL,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE criterios_rubrica (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    tarea_id uuid NOT NULL,
    nombre varchar(200) NOT NULL,
    peso numeric(6,2) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE entregas (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    tarea_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    contenido text,
    archivo_id uuid,
    estado varchar(30) NOT NULL,
    puntual boolean NOT NULL,
    recibida_en timestamptz NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_entrega UNIQUE (tenant_id, tarea_id, estudiante_id)
);

CREATE TABLE calificaciones_tarea (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    entrega_id uuid NOT NULL,
    puntaje numeric(6,2) NOT NULL,
    retroalimentacion text,
    version integer NOT NULL,
    created_at timestamptz NOT NULL
);
