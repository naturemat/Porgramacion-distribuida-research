package ec.mentora.grd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotaRepository extends JpaRepository<Nota, UUID> {
    Optional<Nota> findByIdAndTenantId(UUID id, String tenantId);

    List<Nota> findByTenantIdAndAulaIdAndPeriodoId(String tenantId, UUID aulaId, UUID periodoId);

    Optional<Nota> findByTenantIdAndAulaIdAndEstudianteIdAndComponenteIdAndOrigenAndOrigenId(String tenantId, UUID aulaId, UUID estudianteId, UUID componenteId, String origen, UUID origenId);
}
