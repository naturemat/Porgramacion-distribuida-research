package ec.mentora.aul.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnuncioRepository extends JpaRepository<Anuncio, UUID> {
    List<Anuncio> findByTenantIdAndAulaIdOrderByCreatedAtDesc(String tenantId, UUID aulaId);
}
