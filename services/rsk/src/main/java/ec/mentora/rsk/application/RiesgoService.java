package ec.mentora.rsk.application;

import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import ec.mentora.rsk.application.dto.RskDtos.IntervencionRequest;
import ec.mentora.rsk.application.dto.RskDtos.RevisionRequest;
import ec.mentora.rsk.domain.CasoRiesgo;
import ec.mentora.rsk.domain.CasoRiesgoRepository;
import ec.mentora.rsk.domain.Factor;
import ec.mentora.rsk.domain.FactorRepository;
import ec.mentora.rsk.domain.Intervencion;
import ec.mentora.rsk.domain.IntervencionRepository;
import ec.mentora.rsk.domain.Puntaje;
import ec.mentora.rsk.domain.PuntajeRepository;
import ec.mentora.rsk.domain.Revision;
import ec.mentora.rsk.domain.RevisionRepository;
import ec.mentora.rsk.domain.SenalAcademica;
import ec.mentora.rsk.domain.SenalAcademicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiesgoService {

    private final SenalAcademicaRepository senales;
    private final FactorRepository factores;
    private final PuntajeRepository puntajes;
    private final CasoRiesgoRepository casos;
    private final RevisionRepository revisiones;
    private final IntervencionRepository intervenciones;
    private final OutboxService outbox;

    @Transactional(readOnly = true)
    public List<CasoRiesgo> listarCasos() {
        return casos.findByTenantIdOrderByCreatedAtDesc(TenantContext.require());
    }

    @Transactional
    public CasoRiesgo revisar(UUID casoId, RevisionRequest request) {
        String tenantId = TenantContext.require();
        CasoRiesgo caso = casos.findByIdAndTenantId(casoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("caso no encontrado"));
        Revision revision = new Revision();
        revision.setTenantId(tenantId);
        revision.setCasoId(casoId);
        revision.setRevisorId(request.revisorId());
        revision.setDecision(request.decision());
        revision.setObservacion(request.observacion());
        revisiones.save(revision);
        caso.setEstado("REVISADO");
        casos.save(caso);
        publicar(Topics.RSK_RIESGO, casoId.toString(), "CasoRevisado", Map.of(
            "casoId", casoId.toString(),
            "estudianteId", caso.getEstudianteId().toString(),
            "tipo", caso.getTipo(),
            "decision", request.decision()
        ));
        return caso;
    }

    @Transactional
    public Intervencion intervenir(UUID casoId, IntervencionRequest request) {
        String tenantId = TenantContext.require();
        CasoRiesgo caso = casos.findByIdAndTenantId(casoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("caso no encontrado"));
        Intervencion intervencion = new Intervencion();
        intervencion.setTenantId(tenantId);
        intervencion.setCasoId(casoId);
        intervencion.setDescripcion(request.descripcion());
        intervencion.setResponsableId(request.responsableId());
        intervencion.setFecha(Instant.now());
        intervenciones.save(intervencion);
        publicar(Topics.RSK_RIESGO, casoId.toString(), "IntervencionRegistrada", Map.of(
            "casoId", casoId.toString(),
            "estudianteId", caso.getEstudianteId().toString(),
            "tipo", caso.getTipo()
        ));
        return intervencion;
    }

    @Transactional
    public CasoRiesgo escalar(UUID casoId) {
        String tenantId = TenantContext.require();
        CasoRiesgo caso = casos.findByIdAndTenantId(casoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("caso no encontrado"));
        caso.setEstado("ESCALADO");
        casos.save(caso);
        publicar(Topics.RSK_RIESGO, casoId.toString(), "CasoEscalado", Map.of(
            "casoId", casoId.toString(),
            "estudianteId", caso.getEstudianteId().toString(),
            "tipo", caso.getTipo()
        ));
        return caso;
    }

    @Transactional
    public CasoRiesgo cerrar(UUID casoId) {
        String tenantId = TenantContext.require();
        CasoRiesgo caso = casos.findByIdAndTenantId(casoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("caso no encontrado"));
        caso.setEstado("CERRADO");
        casos.save(caso);
        publicar(Topics.RSK_RIESGO, casoId.toString(), "CasoCerrado", Map.of(
            "casoId", casoId.toString(),
            "estudianteId", caso.getEstudianteId().toString(),
            "tipo", caso.getTipo()
        ));
        return caso;
    }

    @Transactional
    public void onNota(UUID estudianteId, BigDecimal valor) {
        SenalAcademica senal = senal(estudianteId);
        senal.setNotaPromedio(valor);
        senales.save(senal);
        evaluarAcademico(senal);
    }

    @Transactional
    public void onAsistencia(UUID estudianteId, String estado) {
        SenalAcademica senal = senal(estudianteId);
        senal.setClases(senal.getClases() + 1);
        if ("AUSENTE".equals(estado)) {
            senal.setFaltas(senal.getFaltas() + 1);
        }
        senales.save(senal);
        evaluarAcademico(senal);
    }

    @Transactional
    public void onEntrega(UUID estudianteId, boolean puntual) {
        SenalAcademica senal = senal(estudianteId);
        if (!puntual) {
            senal.setEntregasTardias(senal.getEntregasTardias() + 1);
        }
        senales.save(senal);
        evaluarAcademico(senal);
    }

    @Transactional
    public void onConducta(UUID estudianteId, int gravedad) {
        if (gravedad < 3) {
            return;
        }
        BigDecimal score = BigDecimal.valueOf(gravedad * 10L);
        crearCaso(estudianteId, "CONDUCTUAL", score, "Riesgo conductual: gravedad " + gravedad,
            List.of("gravedad_conducta"), List.of(BigDecimal.valueOf(gravedad)));
    }

    private SenalAcademica senal(UUID estudianteId) {
        String tenantId = TenantContext.require();
        return senales.findByTenantIdAndEstudianteId(tenantId, estudianteId).orElseGet(() -> {
            SenalAcademica nueva = new SenalAcademica();
            nueva.setTenantId(tenantId);
            nueva.setEstudianteId(estudianteId);
            nueva.setNotaPromedio(BigDecimal.ZERO);
            nueva.setFaltas(0);
            nueva.setClases(0);
            nueva.setEntregasTardias(0);
            return nueva;
        });
    }

    private void evaluarAcademico(SenalAcademica senal) {
        BigDecimal score = BigDecimal.ZERO;
        List<String> nombres = new ArrayList<>();
        List<BigDecimal> valores = new ArrayList<>();
        double ratio = senal.getClases() == 0 ? 0 : (double) senal.getFaltas() / senal.getClases();
        if (ratio > 0.3) {
            score = score.add(BigDecimal.valueOf(40));
            nombres.add("inasistencia");
            valores.add(BigDecimal.valueOf(ratio * 100).setScale(2, RoundingMode.HALF_UP));
        }
        if (senal.getEntregasTardias() > 2) {
            score = score.add(BigDecimal.valueOf(30));
            nombres.add("entregas_tardias");
            valores.add(BigDecimal.valueOf(senal.getEntregasTardias()));
        }
        if (senal.getNotaPromedio().compareTo(new BigDecimal("6.00")) < 0) {
            score = score.add(BigDecimal.valueOf(30));
            nombres.add("bajo_rendimiento");
            valores.add(senal.getNotaPromedio());
        }
        if (score.compareTo(BigDecimal.valueOf(30)) >= 0) {
            crearCaso(senal.getEstudianteId(), "ACADEMICO", score,
                "Riesgo académico: " + String.join(", ", nombres), nombres, valores);
        }
    }

    private void crearCaso(UUID estudianteId, String tipo, BigDecimal score, String explicacion,
                           List<String> nombres, List<BigDecimal> valores) {
        String tenantId = TenantContext.require();
        if (casos.existsByTenantIdAndEstudianteIdAndTipoAndEstado(tenantId, estudianteId, tipo, "ABIERTO")) {
            return;
        }
        CasoRiesgo caso = new CasoRiesgo();
        caso.setTenantId(tenantId);
        caso.setEstudianteId(estudianteId);
        caso.setTipo(tipo);
        caso.setEstado("ABIERTO");
        caso.setPuntaje(score);
        caso.setExplicacion(explicacion);
        casos.save(caso);
        for (int i = 0; i < nombres.size(); i++) {
            Factor factor = new Factor();
            factor.setTenantId(tenantId);
            factor.setCasoId(caso.getId());
            factor.setNombre(nombres.get(i));
            factor.setValor(valores.get(i));
            factor.setPeso(BigDecimal.ONE);
            factores.save(factor);
        }
        Puntaje puntaje = new Puntaje();
        puntaje.setTenantId(tenantId);
        puntaje.setEstudianteId(estudianteId);
        puntaje.setTipo(tipo);
        puntaje.setValor(score);
        puntaje.setCalculadoEn(Instant.now());
        puntajes.save(puntaje);
        publicar(Topics.RSK_RIESGO, caso.getId().toString(), "CasoCreado", Map.of(
            "casoId", caso.getId().toString(),
            "estudianteId", estudianteId.toString(),
            "tipo", tipo,
            "puntaje", score,
            "explicacion", explicacion
        ));
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "risk-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
