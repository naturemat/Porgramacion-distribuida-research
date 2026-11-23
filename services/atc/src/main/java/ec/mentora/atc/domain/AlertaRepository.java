package ec.mentora.atc.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertaRepository extends JpaRepository<Alerta, UUID> {
    Optional<Alerta> findByIdAndTenantId(UUID id, String tenantId);

    List<Alerta> findByTenantIdAndEstado(String tenantId, String estado);

    boolean existsByTenantIdAndAulaIdAndEstudianteIdAndEstado(String tenantId, UUID aulaId, UUID estudianteId, String estado);
}
