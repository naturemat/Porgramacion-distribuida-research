package ec.mentora.grd.domain;

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
@Table(name = "notas")
public class Nota extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "componente_id", nullable = false)
    private UUID componenteId;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private String origen;

    @Column(name = "origen_id")
    private UUID origenId;

    @Column(nullable = false)
    private String estado;
}
