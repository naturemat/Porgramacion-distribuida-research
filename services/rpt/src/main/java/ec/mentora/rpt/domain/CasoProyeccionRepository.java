package ec.mentora.rpt.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CasoProyeccionRepository extends JpaRepository<CasoProyeccion, UUID> {
    List<CasoProyeccion> findByTenantId(String tenantId);
}
