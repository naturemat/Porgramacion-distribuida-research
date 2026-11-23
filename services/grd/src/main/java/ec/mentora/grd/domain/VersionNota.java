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
@Table(name = "versiones_nota")
public class VersionNota extends BaseEntity {

    @Column(name = "nota_id", nullable = false)
    private UUID notaId;

    @Column(name = "valor_anterior", precision = 6, scale = 2)
    private BigDecimal valorAnterior;

    @Column(name = "valor_nuevo", nullable = false, precision = 6, scale = 2)
    private BigDecimal valorNuevo;

    @Column(nullable = false, columnDefinition = "text")
    private String motivo;

    @Column(name = "creada_por")
    private UUID creadaPor;
}
