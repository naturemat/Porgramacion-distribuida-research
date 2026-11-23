package ec.mentora.cue.domain;

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
@Table(name = "opciones")
public class Opcion extends BaseEntity {

    @Column(name = "pregunta_id", nullable = false)
    private UUID preguntaId;

    @Column(nullable = false, columnDefinition = "text")
    private String texto;

    @Column(nullable = false)
    private boolean correcta;
}
