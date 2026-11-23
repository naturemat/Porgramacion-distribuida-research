package ec.mentora.grd.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.grd.application.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class GrdConsumer {

    private final ObjectMapper objectMapper;
    private final NotaService service;

    @KafkaListener(topics = Topics.AUL_AULA, groupId = "modulo-c.grading-service.consumidor.aula")
    public void aula(String payload) {
        withTenant(payload, root -> {
            if ("AulaCreada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.crearComponentesDefault(Events.uuid(data, "aulaId"), Events.uuid(data, "periodoId"));
            }
        });
    }

    @KafkaListener(topics = Topics.ASG_ENTREGA, groupId = "modulo-c.grading-service.consumidor.asg-entrega")
    public void entrega(String payload) {
        withTenant(payload, root -> {
            if ("EntregaCalificada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.registrarDesdeEvento(
                    Events.uuid(data, "aulaId"),
                    Events.uuid(data, "periodoId"),
                    Events.uuid(data, "estudianteId"),
                    "Tareas",
                    "ASG",
                    Events.uuid(data, "entregaId"),
                    decimal(data, "puntaje"),
                    decimal(data, "puntajeMaximo")
                );
            }
        });
    }

    @KafkaListener(topics = Topics.CUE_INTENTO, groupId = "modulo-c.grading-service.consumidor.cue-intento")
    public void intento(String payload) {
        withTenant(payload, root -> {
            if ("IntentoCalificado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                service.registrarDesdeEvento(
                    Events.uuid(data, "aulaId"),
                    Events.uuid(data, "periodoId"),
                    Events.uuid(data, "estudianteId"),
                    "Cuestionarios",
                    "CUE",
                    Events.uuid(data, "intentoId"),
                    decimal(data, "puntaje"),
                    decimal(data, "puntajeMaximo")
                );
            }
        });
    }

    @KafkaListener(topics = Topics.ATT_ASISTENCIA, groupId = "modulo-c.grading-service.consumidor.att-asistencia")
    public void asistencia(String payload) {
        withTenant(payload, root -> {
            if ("AsistenciaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                String estado = Events.text(data, "estado");
                BigDecimal valor = switch (estado) {
                    case "PRESENTE", "JUSTIFICADO" -> BigDecimal.TEN;
                    case "ATRASO" -> new BigDecimal("5.00");
                    default -> BigDecimal.ZERO;
                };
                service.registrarDesdeEvento(
                    Events.uuid(data, "aulaId"),
                    Events.uuid(data, "periodoId"),
                    Events.uuid(data, "estudianteId"),
                    "Participación",
                    "ATT",
                    Events.uuid(data, "sesionId"),
                    valor,
                    BigDecimal.TEN
                );
            }
        });
    }

    private BigDecimal decimal(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.decimalValue();
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
