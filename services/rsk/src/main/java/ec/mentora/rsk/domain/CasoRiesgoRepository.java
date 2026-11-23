package ec.mentora.rsk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CasoRiesgoRepository extends JpaRepository<CasoRiesgo, UUID> {
    List<CasoRiesgo> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    Optional<CasoRiesgo> findByIdAndTenantId(UUID id, String tenantId);

    boolean existsByTenantIdAndEstudianteIdAndTipoAndEstado(String tenantId, UUID estudianteId, String tipo, String estado);
}
