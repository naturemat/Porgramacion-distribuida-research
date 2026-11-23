package ec.mentora.anl.domain;

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
@Table(name = "hechos_riesgo")
public class HechoRiesgo extends BaseEntity {

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal puntaje;

    @Column(nullable = false)
    private Instant fecha;
}
