package ec.mentora.rsk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SenalAcademicaRepository extends JpaRepository<SenalAcademica, UUID> {
    Optional<SenalAcademica> findByTenantIdAndEstudianteId(String tenantId, UUID estudianteId);
}
