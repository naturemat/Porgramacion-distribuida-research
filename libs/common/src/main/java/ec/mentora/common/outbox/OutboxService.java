package ec.mentora.common.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Uuids;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void enqueue(String topic, String partitionKey, CloudEvent<?> event) {
        OutboxEntry entry = new OutboxEntry();
        entry.setId(Uuids.v7());
        entry.setTenantId(event.tenantId());
        entry.setTopic(topic);
        entry.setPartitionKey(partitionKey);
        entry.setPayload(serialize(event));
        entry.setCreatedAt(Instant.now());
        entry.setAttempts(0);
        repository.save(entry);
    }

    private String serialize(CloudEvent<?> event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("evento no serializable", exception);
        }
    }
}
