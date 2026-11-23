package ec.mentora.anl.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.anl.application.AnaliticaService;
import ec.mentora.anl.domain.HechoAsistencia;
import ec.mentora.anl.domain.HechoAsistenciaRepository;
import ec.mentora.anl.domain.HechoEntrega;
import ec.mentora.anl.domain.HechoEntregaRepository;
import ec.mentora.anl.domain.HechoNota;
import ec.mentora.anl.domain.HechoNotaRepository;
import ec.mentora.anl.domain.HechoRiesgo;
import ec.mentora.anl.domain.HechoRiesgoRepository;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AnlConsumer {

    private final ObjectMapper objectMapper;
    private final AnaliticaService service;
    private final HechoAsistenciaRepository asistencias;
    private final HechoNotaRepository notas;
    private final HechoEntregaRepository entregas;
    private final HechoRiesgoRepository riesgos;

    @KafkaListener(topics = Topics.ATT_ASISTENCIA, groupId = "modulo-c.analytics-service.consumidor.att-asistencia")
    @Transactional
    public void asistencia(String payload) {
        withTenant(payload, root -> {
            if ("AsistenciaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                HechoAsistencia hecho = new HechoAsistencia();
                hecho.setTenantId(TenantContext.require());
                hecho.setAulaId(Events.uuid(data, "aulaId"));
                hecho.setEstudianteId(Events.uuid(data, "estudianteId"));
                hecho.setFecha(LocalDate.now());
                hecho.setEstado(Events.text(data, "estado"));
                asistencias.save(hecho);
                service.publicarMetrica();
            }
        });
    }

    @KafkaListener(topics = Topics.GRD_NOTA, groupId = "modulo-c.analytics-service.consumidor.grd-nota")
    @Transactional
    public void nota(String payload) {
        withTenant(payload, root -> {
            if ("NotaPublicada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                HechoNota hecho = new HechoNota();
                hecho.setTenantId(TenantContext.require());
                hecho.setAulaId(Events.uuid(data, "aulaId"));
                hecho.setEstudianteId(Events.uuid(data, "estudianteId"));
                hecho.setPeriodoId(Events.uuid(data, "periodoId"));
                hecho.setComponente(Events.text(data, "origen"));
                hecho.setValor(decimal(data, "valor"));
                notas.save(hecho);
                service.publicarMetrica();
            }
        });
    }

    @KafkaListener(topics = Topics.ASG_ENTREGA, groupId = "modulo-c.analytics-service.consumidor.asg-entrega")
    @Transactional
    public void entrega(String payload) {
        withTenant(payload, root -> {
            if ("EntregaRecibida".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                HechoEntrega hecho = new HechoEntrega();
                hecho.setTenantId(TenantContext.require());
                hecho.setAulaId(Events.uuid(data, "aulaId"));
                hecho.setEstudianteId(Events.uuid(data, "estudianteId"));
                hecho.setPuntual(Events.bool(data, "puntual"));
                hecho.setFecha(Instant.now());
                entregas.save(hecho);
                service.publicarMetrica();
            }
        });
    }

    @KafkaListener(topics = Topics.RSK_RIESGO, groupId = "modulo-c.analytics-service.consumidor.rsk-riesgo")
    @Transactional
    public void riesgo(String payload) {
        withTenant(payload, root -> {
            if ("CasoCreado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                HechoRiesgo hecho = new HechoRiesgo();
                hecho.setTenantId(TenantContext.require());
                hecho.setEstudianteId(Events.uuid(data, "estudianteId"));
                hecho.setTipo(Events.text(data, "tipo"));
                hecho.setPuntaje(decimal(data, "puntaje"));
                hecho.setFecha(Instant.now());
                riesgos.save(hecho);
                service.publicarMetrica();
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
