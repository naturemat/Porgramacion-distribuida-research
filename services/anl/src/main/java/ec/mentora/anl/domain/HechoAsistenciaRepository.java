package ec.mentora.anl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HechoAsistenciaRepository extends JpaRepository<HechoAsistencia, UUID> {
    List<HechoAsistencia> findByTenantId(String tenantId);

    List<HechoAsistencia> findByTenantIdAndAulaId(String tenantId, UUID aulaId);

    long countByTenantIdAndEstado(String tenantId, String estado);
}
