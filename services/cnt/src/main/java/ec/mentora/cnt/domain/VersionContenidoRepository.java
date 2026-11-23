package ec.mentora.cnt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersionContenidoRepository extends JpaRepository<VersionContenido, UUID> {
    List<VersionContenido> findByTenantIdAndContenidoIdOrderByNumeroAsc(String tenantId, UUID contenidoId);

    long countByTenantIdAndContenidoId(String tenantId, UUID contenidoId);
}
