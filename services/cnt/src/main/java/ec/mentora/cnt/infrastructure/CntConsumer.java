package ec.mentora.cnt.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.cnt.application.ContenidoService;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class CntConsumer {

    private final ObjectMapper objectMapper;
    private final ContenidoService service;

    @KafkaListener(topics = Topics.B_ARCHIVO, groupId = "modulo-c.content-service.consumidor.archivo")
    public void archivo(String payload) {
        withTenant(payload, root -> {
            if ("ArchivoAnalizado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onVeredicto(Events.uuid(data, "archivoId"), Events.text(data, "veredicto"));
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
