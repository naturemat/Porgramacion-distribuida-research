package ec.mentora.cue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntentoRepository extends JpaRepository<Intento, UUID> {
    Optional<Intento> findByIdAndTenantId(UUID id, String tenantId);

    long countByTenantIdAndCuestionarioIdAndEstudianteId(String tenantId, UUID cuestionarioId, UUID estudianteId);
}
