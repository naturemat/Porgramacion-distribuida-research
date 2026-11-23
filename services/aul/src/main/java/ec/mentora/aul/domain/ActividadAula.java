package ec.mentora.aul.domain;

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
@Table(name = "actividad_aula")
public class ActividadAula extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(nullable = false)
    private Instant hora;

    @Column(nullable = false, columnDefinition = "text")
    private String contadores;
}
