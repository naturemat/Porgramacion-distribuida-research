package ec.mentora.grd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromedioRepository extends JpaRepository<Promedio, UUID> {
    Optional<Promedio> findByTenantIdAndAulaIdAndEstudianteIdAndPeriodoId(String tenantId, UUID aulaId, UUID estudianteId, UUID periodoId);

    List<Promedio> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
