package ec.mentora.cnt.domain;

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
@Table(name = "contenidos")
public class Contenido extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "unidad_id")
    private UUID unidadId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String estado;
}
