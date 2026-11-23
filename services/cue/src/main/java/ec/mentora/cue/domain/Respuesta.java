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
@Table(name = "respuestas")
public class Respuesta extends BaseEntity {

    @Column(name = "intento_id", nullable = false)
    private UUID intentoId;

    @Column(name = "pregunta_id", nullable = false)
    private UUID preguntaId;

    @Column(name = "opcion_id")
    private UUID opcionId;

    @Column(columnDefinition = "text")
    private String texto;
}
