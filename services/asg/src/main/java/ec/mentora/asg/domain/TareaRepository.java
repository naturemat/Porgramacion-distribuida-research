package ec.mentora.asg.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TareaRepository extends JpaRepository<Tarea, UUID> {
    List<Tarea> findByTenantIdAndAulaIdOrderByCreatedAtDesc(String tenantId, UUID aulaId);

    Optional<Tarea> findByIdAndTenantId(UUID id, String tenantId);

    List<Tarea> findByTenantIdAndAulaId(String tenantId, UUID aulaId);
}
