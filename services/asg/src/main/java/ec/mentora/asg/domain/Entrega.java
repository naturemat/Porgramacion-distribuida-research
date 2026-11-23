package ec.mentora.asg.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "entregas")
public class Entrega extends BaseEntity {

    @Column(name = "tarea_id", nullable = false)
    private UUID tareaId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(columnDefinition = "text")
    private String contenido;

    @Column(name = "archivo_id")
    private UUID archivoId;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private boolean puntual;

    @Column(name = "recibida_en", nullable = false)
    private Instant recibidaEn;
}
