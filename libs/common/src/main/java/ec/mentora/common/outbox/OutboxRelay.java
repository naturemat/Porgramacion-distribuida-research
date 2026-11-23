package ec.mentora.common.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void relay() {
        List<OutboxEntry> pending = repository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();
        for (OutboxEntry entry : pending) {
            try {
                kafkaTemplate.send(entry.getTopic(), entry.getPartitionKey(), entry.getPayload())
                    .get(5, TimeUnit.SECONDS);
                entry.setPublishedAt(Instant.now());
                repository.save(entry);
            } catch (Exception exception) {
                entry.setAttempts(entry.getAttempts() + 1);
                repository.save(entry);
            }
        }
    }
}
