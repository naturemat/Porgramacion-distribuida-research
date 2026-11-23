package ec.mentora.anl.domain;

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
@Table(name = "hechos_entrega")
public class HechoEntrega extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(nullable = false)
    private boolean puntual;

    @Column(nullable = false)
    private Instant fecha;
}
