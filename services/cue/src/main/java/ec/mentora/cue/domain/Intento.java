package ec.mentora.cue.domain;

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
@Table(name = "intentos")
public class Intento extends BaseEntity {

    @Column(name = "cuestionario_id", nullable = false)
    private UUID cuestionarioId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "iniciado_en", nullable = false)
    private Instant iniciadoEn;

    @Column(name = "enviado_en")
    private Instant enviadoEn;

    @Column(precision = 6, scale = 2)
    private BigDecimal puntaje;
}
