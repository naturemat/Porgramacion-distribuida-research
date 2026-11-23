package ec.mentora.rsk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactorRepository extends JpaRepository<Factor, UUID> {
    List<Factor> findByTenantIdAndCasoId(String tenantId, UUID casoId);
}
