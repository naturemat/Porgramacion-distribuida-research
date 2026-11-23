package ec.mentora.cnt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenciaArchivoRepository extends JpaRepository<ReferenciaArchivo, UUID> {
    Optional<ReferenciaArchivo> findByTenantIdAndArchivoId(String tenantId, UUID archivoId);

    List<ReferenciaArchivo> findByTenantIdAndContenidoId(String tenantId, UUID contenidoId);
}
