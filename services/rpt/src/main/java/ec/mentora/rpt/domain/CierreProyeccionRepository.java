package ec.mentora.rpt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CierreProyeccionRepository extends JpaRepository<CierreProyeccion, UUID> {
    Optional<CierreProyeccion> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
