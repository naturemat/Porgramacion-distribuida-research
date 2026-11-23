package ec.mentora.att.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SesionAsistenciaRepository extends JpaRepository<SesionAsistencia, UUID> {
    Optional<SesionAsistencia> findByIdAndTenantId(UUID id, String tenantId);

    Optional<SesionAsistencia> findByTenantIdAndAulaIdAndFecha(String tenantId, UUID aulaId, LocalDate fecha);

    List<SesionAsistencia> findByTenantIdAndAulaId(String tenantId, UUID aulaId);
}
