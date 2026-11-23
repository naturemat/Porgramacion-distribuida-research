package ec.mentora.aul.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatriculaAulaRepository extends JpaRepository<MatriculaAula, UUID> {
    List<MatriculaAula> findByTenantIdAndAulaId(String tenantId, UUID aulaId);

    Optional<MatriculaAula> findByTenantIdAndAulaIdAndEstudianteId(String tenantId, UUID aulaId, UUID estudianteId);
}
