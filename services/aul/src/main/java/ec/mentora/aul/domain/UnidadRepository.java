package ec.mentora.aul.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnidadRepository extends JpaRepository<Unidad, UUID> {
    List<Unidad> findByTenantIdAndAulaIdOrderByOrdenAsc(String tenantId, UUID aulaId);

    Optional<Unidad> findByIdAndTenantId(UUID id, String tenantId);
}
