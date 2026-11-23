package ec.mentora.grd.application;

import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import ec.mentora.grd.application.dto.GrdDtos.CierreRequest;
import ec.mentora.grd.application.dto.GrdDtos.CorreccionRequest;
import ec.mentora.grd.application.dto.GrdDtos.CrearComponenteRequest;
import ec.mentora.grd.application.dto.GrdDtos.NotaLoteRequest;
import ec.mentora.grd.application.dto.GrdDtos.RegistrarNotaRequest;
import ec.mentora.grd.domain.CierrePeriodo;
import ec.mentora.grd.domain.CierrePeriodoRepository;
import ec.mentora.grd.domain.ComponenteEvaluacion;
import ec.mentora.grd.domain.ComponenteEvaluacionRepository;
import ec.mentora.grd.domain.Nota;
import ec.mentora.grd.domain.NotaRepository;
import ec.mentora.grd.domain.Promedio;
import ec.mentora.grd.domain.PromedioRepository;
import ec.mentora.grd.domain.VersionNota;
import ec.mentora.grd.domain.VersionNotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final ComponenteEvaluacionRepository componentes;
    private final NotaRepository notas;
    private final VersionNotaRepository versiones;
    private final CierrePeriodoRepository cierres;
    private final PromedioRepository promedios;
    private final OutboxService outbox;

    @Transactional
    public ComponenteEvaluacion crearComponente(CrearComponenteRequest request) {
        ComponenteEvaluacion componente = new ComponenteEvaluacion();
        componente.setTenantId(TenantContext.require());
        componente.setAulaId(request.aulaId());
        componente.setPeriodoId(request.periodoId());
        componente.setNombre(request.nombre());
        componente.setPeso(request.peso());
        return componentes.save(componente);
    }

    @Transactional
    public Nota registrar(RegistrarNotaRequest request) {
        String tenantId = TenantContext.require();
        componentes.findByIdAndTenantId(request.componenteId(), tenantId)
            .orElseThrow(() -> ApiException.notFound("componente no encontrado"));
        if (request.origenId() != null) {
            Nota existente = notas
                .findByTenantIdAndAulaIdAndEstudianteIdAndComponenteIdAndOrigenAndOrigenId(
                    tenantId, request.aulaId(), request.estudianteId(), request.componenteId(),
                    request.origen(), request.origenId())
                .orElse(null);
            if (existente != null) {
                return existente;
            }
        }
        Nota nota = new Nota();
        nota.setTenantId(tenantId);
        nota.setAulaId(request.aulaId());
        nota.setPeriodoId(request.periodoId());
        nota.setEstudianteId(request.estudianteId());
        nota.setComponenteId(request.componenteId());
        nota.setValor(request.valor());
        nota.setOrigen(request.origen());
        nota.setOrigenId(request.origenId());
        nota.setEstado("PUBLICADA");
        notas.save(nota);
        publicar(Topics.GRD_NOTA, nota.getId().toString(), "NotaPublicada", Map.of(
            "notaId", nota.getId().toString(),
            "aulaId", nota.getAulaId().toString(),
            "periodoId", nota.getPeriodoId().toString(),
            "estudianteId", nota.getEstudianteId().toString(),
            "componenteId", nota.getComponenteId().toString(),
            "valor", nota.getValor(),
            "origen", nota.getOrigen()
        ));
        return nota;
    }

    @Transactional
    public List<Nota> registrarLote(NotaLoteRequest request) {
        return request.notas().stream().map(this::registrar).toList();
    }

    @Transactional
    public Nota corregir(UUID notaId, CorreccionRequest request) {
        String tenantId = TenantContext.require();
        Nota nota = notas.findByIdAndTenantId(notaId, tenantId)
            .orElseThrow(() -> ApiException.notFound("nota no encontrada"));
        VersionNota version = new VersionNota();
        version.setTenantId(tenantId);
        version.setNotaId(notaId);
        version.setValorAnterior(nota.getValor());
        version.setValorNuevo(request.valor());
        version.setMotivo(request.motivo());
        version.setCreadaPor(request.creadaPor());
        versiones.save(version);
        nota.setValor(request.valor());
        nota.setEstado("CORREGIDA");
        notas.save(nota);
        publicar(Topics.GRD_NOTA, notaId.toString(), "NotaCorregida", Map.of(
            "notaId", notaId.toString(),
            "aulaId", nota.getAulaId().toString(),
            "periodoId", nota.getPeriodoId().toString(),
            "estudianteId", nota.getEstudianteId().toString(),
            "valor", nota.getValor(),
            "motivo", request.motivo()
        ));
        return nota;
    }

    @Transactional
    public CierrePeriodo cerrar(CierreRequest request) {
        String tenantId = TenantContext.require();
        CierrePeriodo cierre = cierres
            .findByTenantIdAndAulaIdAndPeriodoId(tenantId, request.aulaId(), request.periodoId())
            .orElseGet(CierrePeriodo::new);
        cierre.setTenantId(tenantId);
        cierre.setAulaId(request.aulaId());
        cierre.setPeriodoId(request.periodoId());
        cierre.setEstado("CERRADO");
        cierre.setCerradoEn(Instant.now());
        cierres.save(cierre);
        Map<UUID, List<Nota>> porEstudiante = notas
            .findByTenantIdAndAulaIdAndPeriodoId(tenantId, request.aulaId(), request.periodoId())
            .stream()
            .collect(Collectors.groupingBy(Nota::getEstudianteId));
        for (Map.Entry<UUID, List<Nota>> entry : porEstudiante.entrySet()) {
            BigDecimal suma = BigDecimal.ZERO;
            BigDecimal pesos = BigDecimal.ZERO;
            for (Nota nota : entry.getValue()) {
                ComponenteEvaluacion componente = componentes.findByIdAndTenantId(nota.getComponenteId(), tenantId).orElse(null);
                if (componente != null) {
                    suma = suma.add(nota.getValor().multiply(componente.getPeso()));
                    pesos = pesos.add(componente.getPeso());
                }
            }
            BigDecimal promedio = pesos.signum() == 0
                ? BigDecimal.ZERO
                : suma.divide(pesos, 2, RoundingMode.HALF_UP);
            Promedio registro = promedios
                .findByTenantIdAndAulaIdAndEstudianteIdAndPeriodoId(tenantId, request.aulaId(), entry.getKey(), request.periodoId())
                .orElseGet(Promedio::new);
            registro.setTenantId(tenantId);
            registro.setAulaId(request.aulaId());
            registro.setEstudianteId(entry.getKey());
            registro.setPeriodoId(request.periodoId());
            registro.setValor(promedio);
            promedios.save(registro);
            publicar(Topics.GRD_PERIODO, request.aulaId().toString(), "PromedioCalculado", Map.of(
                "aulaId", request.aulaId().toString(),
                "periodoId", request.periodoId().toString(),
                "estudianteId", entry.getKey().toString(),
                "valor", promedio
            ));
        }
        publicar(Topics.GRD_PERIODO, request.aulaId().toString(), "PeriodoCerrado", Map.of(
            "aulaId", request.aulaId().toString(),
            "periodoId", request.periodoId().toString()
        ));
        return cierre;
    }

    @Transactional(readOnly = true)
    public List<Promedio> promedios(UUID aulaId, UUID periodoId) {
        return promedios.findByTenantIdAndAulaIdAndPeriodoId(TenantContext.require(), aulaId, periodoId);
    }

    @Transactional
    public void crearComponentesDefault(UUID aulaId, UUID periodoId) {
        crearComponenteInterno(aulaId, periodoId, "Tareas", new BigDecimal("0.40"));
        crearComponenteInterno(aulaId, periodoId, "Cuestionarios", new BigDecimal("0.30"));
        crearComponenteInterno(aulaId, periodoId, "Participación", new BigDecimal("0.30"));
    }

    @Transactional
    public void registrarDesdeEvento(UUID aulaId, UUID periodoId, UUID estudianteId, String componenteNombre,
                                     String origen, UUID origenId, BigDecimal valor, BigDecimal puntajeMaximo) {
        String tenantId = TenantContext.require();
        ComponenteEvaluacion componente = componentes
            .findByTenantIdAndAulaIdAndPeriodoIdAndNombre(tenantId, aulaId, periodoId, componenteNombre)
            .orElseGet(() -> crearComponenteInterno(aulaId, periodoId, componenteNombre, pesoPorNombre(componenteNombre)));
        BigDecimal nota = puntajeMaximo == null || puntajeMaximo.signum() == 0
            ? valor
            : valor.multiply(BigDecimal.TEN).divide(puntajeMaximo, 2, RoundingMode.HALF_UP);
        if (notas.findByTenantIdAndAulaIdAndEstudianteIdAndComponenteIdAndOrigenAndOrigenId(
                tenantId, aulaId, estudianteId, componente.getId(), origen, origenId).isPresent()) {
            return;
        }
        Nota registro = new Nota();
        registro.setTenantId(tenantId);
        registro.setAulaId(aulaId);
        registro.setPeriodoId(periodoId);
        registro.setEstudianteId(estudianteId);
        registro.setComponenteId(componente.getId());
        registro.setValor(nota);
        registro.setOrigen(origen);
        registro.setOrigenId(origenId);
        registro.setEstado("PUBLICADA");
        notas.save(registro);
        publicar(Topics.GRD_NOTA, registro.getId().toString(), "NotaPublicada", Map.of(
            "notaId", registro.getId().toString(),
            "aulaId", aulaId.toString(),
            "periodoId", periodoId.toString(),
            "estudianteId", estudianteId.toString(),
            "componenteId", componente.getId().toString(),
            "valor", nota,
            "origen", origen
        ));
    }

    private ComponenteEvaluacion crearComponenteInterno(UUID aulaId, UUID periodoId, String nombre, BigDecimal peso) {
        String tenantId = TenantContext.require();
        return componentes.findByTenantIdAndAulaIdAndPeriodoIdAndNombre(tenantId, aulaId, periodoId, nombre)
            .orElseGet(() -> {
                ComponenteEvaluacion componente = new ComponenteEvaluacion();
                componente.setTenantId(tenantId);
                componente.setAulaId(aulaId);
                componente.setPeriodoId(periodoId);
                componente.setNombre(nombre);
                componente.setPeso(peso);
                return componentes.save(componente);
            });
    }

    private BigDecimal pesoPorNombre(String nombre) {
        return switch (nombre) {
            case "Tareas" -> new BigDecimal("0.40");
            case "Cuestionarios" -> new BigDecimal("0.30");
            default -> new BigDecimal("0.30");
        };
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "grading-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
