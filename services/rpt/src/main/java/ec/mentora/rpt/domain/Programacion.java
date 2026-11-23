package ec.mentora.rpt.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "programaciones")
public class Programacion extends BaseEntity {

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String cron;

    @Column(nullable = false)
    private boolean activa;
}
