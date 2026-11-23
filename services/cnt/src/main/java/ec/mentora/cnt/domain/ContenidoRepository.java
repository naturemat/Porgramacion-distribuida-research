package ec.mentora.cnt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContenidoRepository extends JpaRepository<Contenido, UUID> {
    List<Contenido> findByTenantIdAndAulaIdOrderByCreatedAtDesc(String tenantId, UUID aulaId);

    Optional<Contenido> findByIdAndTenantId(UUID id, String tenantId);
}
