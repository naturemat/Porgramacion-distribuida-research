package ec.mentora.att.domain;

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
@Table(name = "justificaciones")
public class Justificacion extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "sesion_id", nullable = false)
    private UUID sesionId;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(nullable = false, columnDefinition = "text")
    private String motivo;

    @Column(name = "evidencia_archivo_id")
    private UUID evidenciaArchivoId;

    @Column(nullable = false)
    private String estado;
}
