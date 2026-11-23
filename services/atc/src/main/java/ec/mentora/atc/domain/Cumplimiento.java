package ec.mentora.atc.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cumplimiento")
public class Cumplimiento extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(nullable = false)
    private int faltas;

    @Column(nullable = false)
    private int clases;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcentaje;

    @Column(nullable = false)
    private String estado;
}
