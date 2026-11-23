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

CREATE TABLE sesiones_asistencia (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    periodo_id uuid NOT NULL,
    fecha date NOT NULL,
    estado varchar(30) NOT NULL,
    abierta_por uuid,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_sesion UNIQUE (tenant_id, aula_id, fecha)
);

CREATE TABLE registros_asistencia (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    sesion_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    estado varchar(30) NOT NULL,
    observacion text,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_registro UNIQUE (tenant_id, sesion_id, estudiante_id)
);

CREATE TABLE justificaciones (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    sesion_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    motivo text NOT NULL,
    evidencia_archivo_id uuid,
    estado varchar(30) NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE matricula_proyeccion (
    id uuid PRIMARY KEY,
    tenant_id varchar(64) NOT NULL,
    aula_id uuid NOT NULL,
    estudiante_id uuid NOT NULL,
    created_at timestamptz NOT NULL,
    CONSTRAINT uq_matricula_proy UNIQUE (tenant_id, aula_id, estudiante_id)
);
