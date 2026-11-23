package ec.mentora.cue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreguntaRepository extends JpaRepository<Pregunta, UUID> {
    List<Pregunta> findByTenantIdAndCuestionarioId(String tenantId, UUID cuestionarioId);
}
