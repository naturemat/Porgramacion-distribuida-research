package ec.mentora.anl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableroRepository extends JpaRepository<Tablero, UUID> {
    List<Tablero> findByTenantId(String tenantId);

    Optional<Tablero> findByIdAndTenantId(UUID id, String tenantId);
}
