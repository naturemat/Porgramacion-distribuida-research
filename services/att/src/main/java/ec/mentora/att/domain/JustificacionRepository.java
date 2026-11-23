package ec.mentora.att.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JustificacionRepository extends JpaRepository<Justificacion, UUID> {
    Optional<Justificacion> findByIdAndTenantId(UUID id, String tenantId);

    List<Justificacion> findByTenantIdAndEstado(String tenantId, String estado);
}
