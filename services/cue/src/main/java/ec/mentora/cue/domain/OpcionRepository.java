package ec.mentora.cue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OpcionRepository extends JpaRepository<Opcion, UUID> {
    List<Opcion> findByTenantIdAndPreguntaId(String tenantId, UUID preguntaId);

    Optional<Opcion> findByIdAndTenantId(UUID id, String tenantId);
}
