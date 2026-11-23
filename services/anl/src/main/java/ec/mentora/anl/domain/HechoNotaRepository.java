package ec.mentora.anl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HechoNotaRepository extends JpaRepository<HechoNota, UUID> {
    List<HechoNota> findByTenantId(String tenantId);

    List<HechoNota> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);
}
