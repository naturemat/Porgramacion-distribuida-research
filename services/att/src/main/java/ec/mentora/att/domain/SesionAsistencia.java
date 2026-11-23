package ec.mentora.att.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sesiones_asistencia")
public class SesionAsistencia extends BaseEntity {

    @Column(name = "aula_id", nullable = false)
    private UUID aulaId;

    @Column(name = "periodo_id", nullable = false)
    private UUID periodoId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String estado;

    @Column(name = "abierta_por")
    private UUID abiertaPor;
}
