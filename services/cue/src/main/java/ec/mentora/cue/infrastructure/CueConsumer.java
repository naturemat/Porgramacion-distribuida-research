package ec.mentora.cue.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.cue.application.CuestionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CueConsumer {

    private final ObjectMapper objectMapper;
    private final CuestionarioService service;

    @KafkaListener(topics = Topics.AUL_AULA, groupId = "modulo-c.quiz-service.consumidor.aula")
    public void aula(String payload) {
        withTenant(payload, root -> {
            if ("AulaCerrada".equals(root.path("type").asText())) {
                service.cerrarPorAula(Events.uuid(root.path("data"), "aulaId"));
            }
        });
    }

    private void withTenant(String payload, Consumer<JsonNode> action) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String tenantId = root.path("tenantId").asText();
            TenantContext.set(tenantId);
            try {
                action.accept(root);
            } finally {
                TenantContext.clear();
            }
        } catch (Exception exception) {
            throw new IllegalStateException("evento inválido", exception);
        }
    }
}
