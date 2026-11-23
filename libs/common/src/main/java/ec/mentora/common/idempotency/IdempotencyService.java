package ec.mentora.common.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.Uuids;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public <T> T execute(String key, Class<T> type, Supplier<T> action) {
        String tenantId = TenantContext.require();
        Optional<IdempotencyRecord> existing = repository.findByTenantIdAndIdempotencyKey(tenantId, key);
        if (existing.isPresent()) {
            return read(existing.get().getResponse(), type);
        }
        T result = action.get();
        IdempotencyRecord record = new IdempotencyRecord();
        record.setId(Uuids.v7());
        record.setTenantId(tenantId);
        record.setIdempotencyKey(key);
        record.setResponse(write(result));
        record.setCreatedAt(Instant.now());
        repository.save(record);
        return result;
    }

    private <T> T read(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("respuesta idempotente ilegible", exception);
        }
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("respuesta no serializable", exception);
        }
    }
}
