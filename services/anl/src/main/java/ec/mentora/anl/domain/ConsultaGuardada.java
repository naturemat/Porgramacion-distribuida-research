package ec.mentora.anl.domain;

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
@Table(name = "consultas_guardadas")
public class ConsultaGuardada extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, columnDefinition = "text")
    private String consulta;
}
