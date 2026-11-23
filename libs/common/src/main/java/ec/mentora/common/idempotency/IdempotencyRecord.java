package ec.mentora.common.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "idempotency_record")
public class IdempotencyRecord {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "key", nullable = false)
    private String idempotencyKey;

    @Column(nullable = false, columnDefinition = "text")
    private String response;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
