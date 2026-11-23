package ec.mentora.rsk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RevisionRepository extends JpaRepository<Revision, UUID> {
    List<Revision> findByTenantIdAndCasoId(String tenantId, UUID casoId);
}
