package ec.mentora.rpt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReporteRepository extends JpaRepository<Reporte, UUID> {
    Optional<Reporte> findByIdAndTenantId(UUID id, String tenantId);

    List<Reporte> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
