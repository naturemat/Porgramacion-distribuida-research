package ec.mentora.grd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CierrePeriodoRepository extends JpaRepository<CierrePeriodo, UUID> {
    Optional<CierrePeriodo> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
