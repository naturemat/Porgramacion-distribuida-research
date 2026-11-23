package ec.mentora.rsk.domain;

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
@Table(name = "revisiones")
public class Revision extends BaseEntity {

    @Column(name = "caso_id", nullable = false)
    private UUID casoId;

    @Column(name = "revisor_id", nullable = false)
    private UUID revisorId;

    @Column(nullable = false)
    private String decision;

    @Column(columnDefinition = "text")
    private String observacion;
}
