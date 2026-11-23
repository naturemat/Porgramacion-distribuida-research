package ec.mentora.rpt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsistenciaProyeccionRepository extends JpaRepository<AsistenciaProyeccion, UUID> {
    Optional<AsistenciaProyeccion> findByTenantIdAndAulaIdAndEstudianteId(String tenantId, UUID aulaId, UUID estudianteId);

    List<AsistenciaProyeccion> findByTenantIdAndAulaId(String tenantId, UUID aulaId);
}
