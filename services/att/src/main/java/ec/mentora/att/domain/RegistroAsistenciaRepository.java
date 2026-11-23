package ec.mentora.att.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, UUID> {
    List<RegistroAsistencia> findByTenantIdAndSesionId(String tenantId, UUID sesionId);

    Optional<RegistroAsistencia> findByTenantIdAndSesionIdAndEstudianteId(String tenantId, UUID sesionId, UUID estudianteId);
}
