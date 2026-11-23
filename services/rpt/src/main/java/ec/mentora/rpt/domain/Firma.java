package ec.mentora.rpt.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "firmas")
public class Firma extends BaseEntity {

    @Column(name = "reporte_id", nullable = false)
    private UUID reporteId;

    @Column(name = "firmante_id", nullable = false)
    private UUID firmanteId;

    @Column(nullable = false)
    private String hash;
}
