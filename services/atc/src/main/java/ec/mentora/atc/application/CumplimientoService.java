package ec.mentora.atc.application;

import ec.mentora.atc.application.dto.AtcDtos.CrearUmbralRequest;
import ec.mentora.atc.domain.Alerta;
import ec.mentora.atc.domain.AlertaRepository;
import ec.mentora.atc.domain.Cumplimiento;
import ec.mentora.atc.domain.CumplimientoRepository;
import ec.mentora.atc.domain.Umbral;
import ec.mentora.atc.domain.UmbralRepository;
import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CumplimientoService {

    private final UmbralRepository umbrales;
    private final CumplimientoRepository cumplimientos;
    private final AlertaRepository alertas;
    private final OutboxService outbox;

    @Transactional
    public Umbral crearUmbral(CrearUmbralRequest request) {
        String tenantId = TenantContext.require();
        int version = umbrales.findTopByTenantIdOrderByVersionDesc(tenantId).map(u -> u.getVersion() + 1).orElse(1);
        Umbral umbral = new Umbral();
        umbral.setTenantId(tenantId);
        umbral.setNombre(request.nombre());
        umbral.setPorcentaje(request.porcentaje());
        umbral.setVigenteDesde(Instant.now());
        umbral.setVersion(version);
        return umbrales.save(umbral);
    }

    @Transactional(readOnly = true)
    public List<Cumplimiento> listar(UUID aulaId) {
        return cumplimientos.findByTenantIdAndAulaId(TenantContext.require(), aulaId);
    }

    @Transactional(readOnly = true)
    public List<Alerta> alertas() {
        return alertas.findByTenantIdAndEstado(TenantContext.require(), "EMITIDA");
    }

    @Transactional
    public Alerta resolver(UUID alertaId) {
        String tenantId = TenantContext.require();
        Alerta alerta = alertas.findByIdAndTenantId(alertaId, tenantId)
            .orElseThrow(() -> ApiException.notFound("alerta no encontrada"));
        alerta.setEstado("RESUELTA");
        alertas.save(alerta);
        publicar(Topics.ATC_CUMPLIMIENTO, alertaId.toString(), "AlertaResuelta", Map.of(
            "alertaId", alertaId.toString(),
            "aulaId", alerta.getAulaId().toString(),
            "estudianteId", alerta.getEstudianteId().toString()
        ));
        return alerta;
    }

    @Transactional
    public void onAsistencia(UUID aulaId, UUID estudianteId, String estado) {
        String tenantId = TenantContext.require();
        Cumplimiento cumplimiento = cumplimientos
            .findByTenantIdAndAulaIdAndEstudianteId(tenantId, aulaId, estudianteId)
            .orElseGet(() -> nuevo(tenantId, aulaId, estudianteId));
        cumplimiento.setClases(cumplimiento.getClases() + 1);
        if ("AUSENTE".equals(estado)) {
            cumplimiento.setFaltas(cumplimiento.getFaltas() + 1);
        }
        recalcular(cumplimiento);
        cumplimientos.save(cumplimiento);
        evaluarAlerta(cumplimiento);
        publicar(Topics.ATC_CUMPLIMIENTO, aulaId.toString(), "CumplimientoActualizado", Map.of(
            "aulaId", aulaId.toString(),
            "estudianteId", estudianteId.toString(),
            "faltas", cumplimiento.getFaltas(),
            "clases", cumplimiento.getClases(),
            "porcentaje", cumplimiento.getPorcentaje()
        ));
    }

    @Transactional
    public void onJustificacionAprobada(UUID aulaId, UUID estudianteId) {
        String tenantId = TenantContext.require();
        cumplimientos.findByTenantIdAndAulaIdAndEstudianteId(tenantId, aulaId, estudianteId)
            .ifPresent(cumplimiento -> {
                if (cumplimiento.getFaltas() > 0) {
                    cumplimiento.setFaltas(cumplimiento.getFaltas() - 1);
                    recalcular(cumplimiento);
                    cumplimientos.save(cumplimiento);
                    publicar(Topics.ATC_CUMPLIMIENTO, aulaId.toString(), "CumplimientoActualizado", Map.of(
                        "aulaId", aulaId.toString(),
                        "estudianteId", estudianteId.toString(),
                        "faltas", cumplimiento.getFaltas(),
                        "clases", cumplimiento.getClases(),
                        "porcentaje", cumplimiento.getPorcentaje()
                    ));
                }
            });
    }

    private Cumplimiento nuevo(String tenantId, UUID aulaId, UUID estudianteId) {
        Cumplimiento cumplimiento = new Cumplimiento();
        cumplimiento.setTenantId(tenantId);
        cumplimiento.setAulaId(aulaId);
        cumplimiento.setEstudianteId(estudianteId);
        cumplimiento.setFaltas(0);
        cumplimiento.setClases(0);
        cumplimiento.setPorcentaje(BigDecimal.ZERO);
        cumplimiento.setEstado("ACTUALIZADO");
        return cumplimiento;
    }

    private void recalcular(Cumplimiento cumplimiento) {
        BigDecimal porcentaje = cumplimiento.getClases() == 0
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(cumplimiento.getFaltas() * 100.0 / cumplimiento.getClases())
                .setScale(2, RoundingMode.HALF_UP);
        cumplimiento.setPorcentaje(porcentaje);
        cumplimiento.setEstado("ACTUALIZADO");
    }

    private void evaluarAlerta(Cumplimiento cumplimiento) {
        String tenantId = cumplimiento.getTenantId();
        Umbral umbral = umbrales.findTopByTenantIdOrderByVersionDesc(tenantId).orElse(null);
        if (umbral == null || cumplimiento.getPorcentaje().compareTo(umbral.getPorcentaje()) < 0) {
            return;
        }
        boolean activa = alertas.existsByTenantIdAndAulaIdAndEstudianteIdAndEstado(
            tenantId, cumplimiento.getAulaId(), cumplimiento.getEstudianteId(), "EMITIDA");
        if (activa) {
            return;
        }
        Alerta alerta = new Alerta();
        alerta.setTenantId(tenantId);
        alerta.setAulaId(cumplimiento.getAulaId());
        alerta.setEstudianteId(cumplimiento.getEstudianteId());
        alerta.setTipo("INASISTENCIA");
        alerta.setMensaje("inasistencia del " + cumplimiento.getPorcentaje() + " por ciento");
        alerta.setEstado("EMITIDA");
        alertas.save(alerta);
        publicar(Topics.ATC_CUMPLIMIENTO, alerta.getId().toString(), "AlertaEmitida", Map.of(
            "alertaId", alerta.getId().toString(),
            "aulaId", alerta.getAulaId().toString(),
            "estudianteId", alerta.getEstudianteId().toString(),
            "tipo", alerta.getTipo(),
            "mensaje", alerta.getMensaje()
        ));
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "attendance-compliance-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
