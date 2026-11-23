package ec.mentora.rpt.domain;

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
@Table(name = "reportes")
public class Reporte extends BaseEntity {

    @Column(nullable = false)
    private String tipo;

    @Column(name = "aula_id")
    private UUID aulaId;

    @Column(name = "periodo_id")
    private UUID periodoId;

    @Column(nullable = false)
    private String estado;

    @Column(columnDefinition = "text")
    private String contenido;

    @Column(name = "generado_en")
    private Instant generadoEn;
}
