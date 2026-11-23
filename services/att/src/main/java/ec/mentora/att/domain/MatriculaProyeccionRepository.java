package ec.mentora.att.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatriculaProyeccionRepository extends JpaRepository<MatriculaProyeccion, UUID> {
    List<MatriculaProyeccion> findByTenantIdAndAulaId(String tenantId, UUID aulaId);

    Optional<MatriculaProyeccion> findByTenantIdAndAulaIdAndEstudianteId(String tenantId, UUID aulaId, UUID estudianteId);
}
