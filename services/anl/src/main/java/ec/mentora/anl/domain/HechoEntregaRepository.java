package ec.mentora.anl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HechoEntregaRepository extends JpaRepository<HechoEntrega, UUID> {
    List<HechoEntrega> findByTenantId(String tenantId);

    List<HechoEntrega> findByTenantIdAndAulaId(String tenantId, UUID aulaId);

    long countByTenantIdAndPuntual(String tenantId, boolean puntual);
}
