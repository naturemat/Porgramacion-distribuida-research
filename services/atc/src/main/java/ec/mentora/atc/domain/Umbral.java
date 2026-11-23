package ec.mentora.atc.domain;

import ec.mentora.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "umbrales")
public class Umbral extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "vigente_desde", nullable = false)
    private Instant vigenteDesde;

    @Column(nullable = false)
    private int version;
}
