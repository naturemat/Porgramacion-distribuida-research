package ec.mentora.asg.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalificacionTareaRepository extends JpaRepository<CalificacionTarea, UUID> {
    List<CalificacionTarea> findByTenantIdAndEntregaIdOrderByVersionDesc(String tenantId, UUID entregaId);
}
