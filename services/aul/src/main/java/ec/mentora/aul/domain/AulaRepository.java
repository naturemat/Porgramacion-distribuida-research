package ec.mentora.aul.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AulaRepository extends JpaRepository<Aula, UUID> {
    List<Aula> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    Optional<Aula> findByIdAndTenantId(UUID id, String tenantId);

    List<Aula> findByTenantIdAndPeriodoId(String tenantId, UUID periodoId);
}
