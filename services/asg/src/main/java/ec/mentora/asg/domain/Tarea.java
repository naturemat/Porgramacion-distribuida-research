package ec.mentora.asg.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tareas")
public class Tarea extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "unidad_id")
    private UUID unidadId;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(name = "fecha_limite", nullable = false)
    private Instant fechaLimite;

    @Column(name = "puntaje_maximo", nullable = false, precision = 6, scale = 2)
    private BigDecimal puntajeMaximo;

    @Column(nullable = false)
    private String estado;
}
