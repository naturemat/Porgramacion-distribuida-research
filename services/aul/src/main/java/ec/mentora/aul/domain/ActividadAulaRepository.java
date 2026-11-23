package ec.mentora.aul.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActividadAulaRepository extends JpaRepository<ActividadAula, UUID> {
    List<ActividadAula> findByTenantIdAndAulaIdAndHoraBetween(String tenantId, UUID aulaId, Instant desde, Instant hasta);
}
