package ec.mentora.aul.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.aul.application.AulaService;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AulConsumer {

    private final ObjectMapper objectMapper;
    private final AulaService service;

    @KafkaListener(topics = Topics.A_PERIODO, groupId = "modulo-c.classroom-service.consumidor.periodo")
    public void periodo(String payload) {
        withTenant(payload, root -> {
            if ("PeriodoCerrado".equals(root.path("type").asText())) {
                service.cerrarPorPeriodo(Events.uuid(root.path("data"), "periodoId"));
            }
        });
    }

    @KafkaListener(topics = Topics.B_MATRICULA, groupId = "modulo-c.classroom-service.consumidor.matricula")
    public void matricula(String payload) {
        withTenant(payload, root -> {
            if ("DistributivoPublicado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.crearDesdeDistributivo(
                    Events.uuid(data, "aulaId"),
                    Events.text(data, "nombre"),
                    Events.uuid(data, "periodoId")
                );
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
