package ec.mentora.grd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersionNotaRepository extends JpaRepository<VersionNota, UUID> {
    List<VersionNota> findByTenantIdAndNotaIdOrderByCreatedAtAsc(String tenantId, UUID notaId);
}
