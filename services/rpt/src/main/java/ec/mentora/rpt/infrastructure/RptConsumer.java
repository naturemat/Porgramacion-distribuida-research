package ec.mentora.rpt.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.mentora.common.event.Events;
import ec.mentora.common.event.Topics;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.rpt.domain.AsistenciaProyeccion;
import ec.mentora.rpt.domain.AsistenciaProyeccionRepository;
import ec.mentora.rpt.domain.CasoProyeccion;
import ec.mentora.rpt.domain.CasoProyeccionRepository;
import ec.mentora.rpt.domain.CierreProyeccion;
import ec.mentora.rpt.domain.CierreProyeccionRepository;
import ec.mentora.rpt.domain.NotaProyeccion;
import ec.mentora.rpt.domain.NotaProyeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RptConsumer {

    private final ObjectMapper objectMapper;
    private final NotaProyeccionRepository notas;
    private final AsistenciaProyeccionRepository asistencias;
    private final CasoProyeccionRepository casos;
    private final CierreProyeccionRepository cierres;

    @KafkaListener(topics = Topics.GRD_NOTA, groupId = "modulo-c.reporting-service.consumidor.grd-nota")
    @Transactional
    public void nota(String payload) {
        withTenant(payload, root -> {
            if ("NotaPublicada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                String tenantId = TenantContext.require();
                NotaProyeccion nota = notas
                    .findByTenantIdAndAulaIdAndEstudianteIdAndPeriodoId(
                        tenantId, Events.uuid(data, "aulaId"), Events.uuid(data, "estudianteId"), Events.uuid(data, "periodoId"))
                    .orElseGet(NotaProyeccion::new);
                nota.setTenantId(tenantId);
                nota.setAulaId(Events.uuid(data, "aulaId"));
                nota.setEstudianteId(Events.uuid(data, "estudianteId"));
                nota.setPeriodoId(Events.uuid(data, "periodoId"));
                nota.setValor(decimal(data, "valor"));
                notas.save(nota);
            }
        });
    }

    @KafkaListener(topics = Topics.ATT_ASISTENCIA, groupId = "modulo-c.reporting-service.consumidor.att-asistencia")
    @Transactional
    public void asistencia(String payload) {
        withTenant(payload, root -> {
            if ("AsistenciaRegistrada".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                String tenantId = TenantContext.require();
                AsistenciaProyeccion asistencia = asistencias
                    .findByTenantIdAndAulaIdAndEstudianteId(
                        tenantId, Events.uuid(data, "aulaId"), Events.uuid(data, "estudianteId"))
                    .orElseGet(AsistenciaProyeccion::new);
                asistencia.setTenantId(tenantId);
                asistencia.setAulaId(Events.uuid(data, "aulaId"));
                asistencia.setEstudianteId(Events.uuid(data, "estudianteId"));
                asistencia.setClases(asistencia.getClases() + 1);
                if ("AUSENTE".equals(Events.text(data, "estado"))) {
                    asistencia.setFaltas(asistencia.getFaltas() + 1);
                }
                asistencias.save(asistencia);
            }
        });
    }

    @KafkaListener(topics = Topics.GRD_PERIODO, groupId = "modulo-c.reporting-service.consumidor.grd-periodo")
    @Transactional
    public void periodo(String payload) {
        withTenant(payload, root -> {
            if ("PeriodoCerrado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                String tenantId = TenantContext.require();
                CierreProyeccion cierre = cierres
                    .findByTenantIdAndAulaIdAndPeriodoId(
                        tenantId, Events.uuid(data, "aulaId"), Events.uuid(data, "periodoId"))
                    .orElseGet(CierreProyeccion::new);
                cierre.setTenantId(tenantId);
                cierre.setAulaId(Events.uuid(data, "aulaId"));
                cierre.setPeriodoId(Events.uuid(data, "periodoId"));
                cierre.setCerradoEn(Instant.now());
                cierres.save(cierre);
            }
        });
    }

    @KafkaListener(topics = Topics.RSK_RIESGO, groupId = "modulo-c.reporting-service.consumidor.rsk-riesgo")
    @Transactional
    public void riesgo(String payload) {
        withTenant(payload, root -> {
            if ("CasoCreado".equals(root.path("type").asText())) {
                JsonNode data = root.path("data");
                CasoProyeccion caso = new CasoProyeccion();
                caso.setTenantId(TenantContext.require());
                caso.setEstudianteId(Events.uuid(data, "estudianteId"));
                caso.setTipo(Events.text(data, "tipo"));
                caso.setPuntaje(decimal(data, "puntaje"));
                casos.save(caso);
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
