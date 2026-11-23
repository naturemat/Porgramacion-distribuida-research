package ec.mentora.rsk.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.rsk.application.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RskConsumer {

    private final ObjectMapper objectMapper;
    private final RiesgoService service;

    @KafkaListener(topics = Topics.GRD_NOTA, groupId = "modulo-c.risk-service.consumidor.grd-nota")
    public void nota(String payload) {
        withTenant(payload, root -> {
            if ("NotaPublicada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onNota(Events.uuid(data, "estudianteId"), decimal(data, "valor"));
            }
        });
    }

    @KafkaListener(topics = Topics.ATT_ASISTENCIA, groupId = "modulo-c.risk-service.consumidor.att-asistencia")
    public void asistencia(String payload) {
        withTenant(payload, root -> {
            if ("AsistenciaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onAsistencia(Events.uuid(data, "estudianteId"), Events.text(data, "estado"));
            }
        });
    }

    @KafkaListener(topics = Topics.ASG_ENTREGA, groupId = "modulo-c.risk-service.consumidor.asg-entrega")
    public void entrega(String payload) {
        withTenant(payload, root -> {
            if ("EntregaRecibida".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onEntrega(Events.uuid(data, "estudianteId"), Events.bool(data, "puntual"));
            }
        });
    }

    @KafkaListener(topics = Topics.B_CONDUCTA, groupId = "modulo-c.risk-service.consumidor.conducta")
    public void conducta(String payload) {
        withTenant(payload, root -> {
            if ("ConductaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.onConducta(Events.uuid(data, "estudianteId"), Events.integer(data, "gravedad"));
            }
        });
    }

    private BigDecimal decimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? BigDecimal.ZERO : value.decimalValue();
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
