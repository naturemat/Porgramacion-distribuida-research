package ec.mentora.rsk.domain;

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
@Table(name = "intervenciones")
public class Intervencion extends BaseEntity {

    @Column(name = "caso_id", nullable = false)
    private UUID casoId;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "responsable_id", nullable = false)
    private UUID responsableId;

    @Column(nullable = false)
    private Instant fecha;
}
