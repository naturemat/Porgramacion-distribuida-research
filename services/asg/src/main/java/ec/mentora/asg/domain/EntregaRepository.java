package ec.mentora.asg.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
    Optional<Entrega> findByIdAndTenantId(UUID id, String tenantId);

    Optional<Entrega> findByTenantIdAndTareaIdAndEstudianteId(String tenantId, UUID tareaId, UUID estudianteId);

    List<Entrega> findByTenantIdAndTareaId(String tenantId, UUID tareaId);
}
