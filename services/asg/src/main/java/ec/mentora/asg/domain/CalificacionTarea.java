package ec.mentora.asg.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "calificaciones_tarea")
public class CalificacionTarea extends BaseEntity {

    @Column(name = "entrega_id", nullable = false)
    private UUID entregaId;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal puntaje;

    @Column(columnDefinition = "text")
    private String retroalimentacion;

    @Column(nullable = false)
    private int version;
}
