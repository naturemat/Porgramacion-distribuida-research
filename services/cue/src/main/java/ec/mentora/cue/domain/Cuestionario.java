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
@Table(name = "cuestionarios")
public class Cuestionario extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "unidad_id")
    private UUID unidadId;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String estado;

    @Column(name = "intentos_maximos", nullable = false)
    private int intentosMaximos;
}
