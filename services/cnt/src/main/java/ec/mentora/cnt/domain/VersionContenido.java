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
@Table(name = "versiones_contenido")
public class VersionContenido extends BaseEntity {

    @Column(name = "contenido_id", nullable = false)
    private UUID contenidoId;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, columnDefinition = "text")
    private String cuerpo;

    @Column(name = "archivo_id")
    private UUID archivoId;

    @Column(nullable = false)
    private String estado;
}
