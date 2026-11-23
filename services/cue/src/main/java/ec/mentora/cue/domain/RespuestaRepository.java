package ec.mentora.cue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RespuestaRepository extends JpaRepository<Respuesta, UUID> {
    Optional<Respuesta> findByTenantIdAndIntentoIdAndPreguntaId(String tenantId, UUID intentoId, UUID preguntaId);
}
