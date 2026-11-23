package ec.mentora.rsk.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PuntajeRepository extends JpaRepository<Puntaje, UUID> {
    List<Puntaje> findByTenantIdAndEstudianteId(String tenantId, UUID estudianteId);
}
