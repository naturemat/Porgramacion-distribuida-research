package ec.mentora.atc.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CumplimientoRepository extends JpaRepository<Cumplimiento, UUID> {
    Optional<Cumplimiento> findByTenantIdAndAulaIdAndEstudianteId(String tenantId, UUID aulaId, UUID estudianteId);

    List<Cumplimiento> findByTenantIdAndAulaId(String tenantId, UUID aulaId);
}
