package ec.mentora.aul.domain;

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
@Table(name = "aulas")
public class Aula extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_inicio")
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;
}
