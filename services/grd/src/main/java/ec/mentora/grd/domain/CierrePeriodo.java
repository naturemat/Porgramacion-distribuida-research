package ec.mentora.grd.domain;

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
@Table(name = "cierres_periodo")
public class CierrePeriodo extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "cerrado_en")
    private Instant cerradoEn;
}
