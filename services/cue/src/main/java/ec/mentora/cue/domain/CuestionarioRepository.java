package ec.mentora.cue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuestionarioRepository extends JpaRepository<Cuestionario, UUID> {
    List<Cuestionario> findByTenantIdAndAulaIdOrderByCreatedAtDesc(String tenantId, UUID aulaId);

    Optional<Cuestionario> findByIdAndTenantId(UUID id, String tenantId);

    List<Cuestionario> findByTenantIdAndAulaId(String tenantId, UUID aulaId);
}
