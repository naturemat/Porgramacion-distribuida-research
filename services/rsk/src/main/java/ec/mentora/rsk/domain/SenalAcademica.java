package ec.mentora.rsk.domain;

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
@Table(name = "senales_academicas")
public class SenalAcademica extends BaseEntity {

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "nota_promedio", nullable = false, precision = 6, scale = 2)
    private BigDecimal notaPromedio;

    @Column(nullable = false)
    private int faltas;

    @Column(nullable = false)
    private int clases;

    @Column(name = "entregas_tardias", nullable = false)
    private int entregasTardias;
}
