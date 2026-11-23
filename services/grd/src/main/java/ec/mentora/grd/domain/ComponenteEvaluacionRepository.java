package ec.mentora.grd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComponenteEvaluacionRepository extends JpaRepository<ComponenteEvaluacion, UUID> {
    Optional<ComponenteEvaluacion> findByIdAndTenantId(UUID id, String tenantId);

    Optional<ComponenteEvaluacion> findByTenantIdAndAulaIdAndPeriodoIdAndNombre(String tenantId, UUID aulaId, UUID periodoId, String nombre);

    List<ComponenteEvaluacion> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
