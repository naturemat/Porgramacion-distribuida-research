package ec.mentora.atc.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.atc.application.CumplimientoService;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AtcConsumer {

    private final ObjectMapper objectMapper;
    private final CumplimientoService service;

    @KafkaListener(topics = Topics.ATT_ASISTENCIA, groupId = "modulo-c.attendance-compliance-service.consumidor.asistencia")
    public void asistencia(String payload) {
        withTenant(payload, root -> {
            if ("AsistenciaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onAsistencia(
                    Events.uuid(data, "aulaId"),
                    Events.uuid(data, "estudianteId"),
                    Events.text(data, "estado")
                );
            }
        });
    }

    @KafkaListener(topics = Topics.ATT_JUSTIFICACION, groupId = "modulo-c.attendance-compliance-service.consumidor.justificacion")
    public void justificacion(String payload) {
        withTenant(payload, root -> {
            if ("JustificacionAprobada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onJustificacionAprobada(Events.uuid(data, "aulaId"), Events.uuid(data, "estudianteId"));
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
