package ec.mentora.att.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.att.application.AsistenciaService;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AttConsumer {

    private final ObjectMapper objectMapper;
    private final AsistenciaService service;

    @KafkaListener(topics = Topics.AUL_AULA, groupId = "modulo-c.attendance-service.consumidor.aula")
    public void aula(String payload) {
        withTenant(payload, root -> {
            String type = root.path("type").asText();
            JsonNode data = root.path("data");
            if ("EstudianteMatriculado".equals(type)) {
                service.proyectarMatricula(Events.uuid(data, "aulaId"), Events.uuid(data, "estudianteId"));
            }
            if ("EstudianteRetirado".equals(type)) {
                service.retirarMatricula(Events.uuid(data, "aulaId"), Events.uuid(data, "estudianteId"));
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
