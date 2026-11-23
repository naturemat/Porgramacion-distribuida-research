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
@Table(name = "referencias_archivo")
public class ReferenciaArchivo extends BaseEntity {

    @Column(name = "contenido_id", nullable = false)
    private UUID contenidoId;

    @Column(name = "archivo_id", nullable = false)
    private UUID archivoId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "tipo_mime", nullable = false)
    private String tipoMime;

    @Column(nullable = false)
    private long tamano;

    @Column(nullable = false)
    private String estado;
}
