package ec.mentora.rpt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotaProyeccionRepository extends JpaRepository<NotaProyeccion, UUID> {
    Optional<NotaProyeccion> findByTenantIdAndAulaIdAndEstudianteIdAndPeriodoId(String tenantId, UUID aulaId, UUID estudianteId, UUID periodoId);

    List<NotaProyeccion> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
