package ec.mentora.att.application;

import ec.mentora.att.application.dto.AttDtos.AbrirSesionRequest;
import ec.mentora.att.application.dto.AttDtos.JustificacionRequest;
import ec.mentora.att.application.dto.AttDtos.RegistroRequest;
import ec.mentora.att.domain.Justificacion;
import ec.mentora.att.domain.JustificacionRepository;
import ec.mentora.att.domain.MatriculaProyeccion;
import ec.mentora.att.domain.MatriculaProyeccionRepository;
import ec.mentora.att.domain.RegistroAsistencia;
import ec.mentora.att.domain.RegistroAsistenciaRepository;
import ec.mentora.att.domain.SesionAsistencia;
import ec.mentora.att.domain.SesionAsistenciaRepository;
import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final SesionAsistenciaRepository sesiones;
    private final RegistroAsistenciaRepository registros;
    private final JustificacionRepository justificaciones;
    private final MatriculaProyeccionRepository matriculas;
    private final OutboxService outbox;

    @Transactional
    public SesionAsistencia abrir(AbrirSesionRequest request) {
        String tenantId = TenantContext.require();
        SesionAsistencia sesion = sesiones
            .findByTenantIdAndAulaIdAndFecha(tenantId, request.aulaId(), request.fecha())
            .orElseGet(SesionAsistencia::new);
        sesion.setTenantId(tenantId);
        sesion.setAulaId(request.aulaId());
        sesion.setPeriodoId(request.periodoId());
        sesion.setFecha(request.fecha());
        sesion.setEstado("ABIERTA");
        sesion.setAbiertaPor(request.abiertaPor());
        sesiones.save(sesion);
        publicar(Topics.ATT_ASISTENCIA, sesion.getId().toString(), "SesionAbierta", Map.of(
            "sesionId", sesion.getId().toString(),
            "aulaId", sesion.getAulaId().toString(),
            "periodoId", sesion.getPeriodoId().toString(),
            "fecha", sesion.getFecha().toString()
        ));
        return sesion;
    }

    @Transactional
    public RegistroAsistencia registrar(UUID sesionId, RegistroRequest request) {
        String tenantId = TenantContext.require();
        SesionAsistencia sesion = sesiones.findByIdAndTenantId(sesionId, tenantId)
            .orElseThrow(() -> ApiException.notFound("sesión no encontrada"));
        if ("CERRADA".equals(sesion.getEstado())) {
            throw ApiException.conflict("la sesión está cerrada");
        }
        RegistroAsistencia registro = registros
            .findByTenantIdAndSesionIdAndEstudianteId(tenantId, sesionId, request.estudianteId())
            .orElseGet(RegistroAsistencia::new);
        registro.setTenantId(tenantId);
        registro.setSesionId(sesionId);
        registro.setEstudianteId(request.estudianteId());
        registro.setEstado(request.estado());
        registro.setObservacion(request.observacion());
        registros.save(registro);
        publicar(Topics.ATT_ASISTENCIA, sesionId.toString(), "AsistenciaRegistrada", Map.of(
            "sesionId", sesionId.toString(),
            "aulaId", sesion.getAulaId().toString(),
            "periodoId", sesion.getPeriodoId().toString(),
            "estudianteId", request.estudianteId().toString(),
            "estado", request.estado()
        ));
        return registro;
    }

    @Transactional
    public SesionAsistencia cerrar(UUID sesionId) {
        String tenantId = TenantContext.require();
        SesionAsistencia sesion = sesiones.findByIdAndTenantId(sesionId, tenantId)
            .orElseThrow(() -> ApiException.notFound("sesión no encontrada"));
        sesion.setEstado("CERRADA");
        sesiones.save(sesion);
        publicar(Topics.ATT_ASISTENCIA, sesionId.toString(), "SesionCerrada", Map.of(
            "sesionId", sesionId.toString(),
            "aulaId", sesion.getAulaId().toString(),
            "periodoId", sesion.getPeriodoId().toString()
        ));
        return sesion;
    }

    @Transactional
    public Justificacion solicitar(JustificacionRequest request) {
        String tenantId = TenantContext.require();
        SesionAsistencia sesion = sesiones.findByIdAndTenantId(request.sesionId(), tenantId)
            .orElseThrow(() -> ApiException.notFound("sesión no encontrada"));
        Justificacion justificacion = new Justificacion();
        justificacion.setTenantId(tenantId);
        justificacion.setAulaId(sesion.getAulaId());
        justificacion.setSesionId(request.sesionId());
        justificacion.setEstudianteId(request.estudianteId());
        justificacion.setMotivo(request.motivo());
        justificacion.setEvidenciaArchivoId(request.evidenciaArchivoId());
        justificacion.setEstado("SOLICITADA");
        justificaciones.save(justificacion);
        publicar(Topics.ATT_JUSTIFICACION, justificacion.getId().toString(), "JustificacionSolicitada", Map.of(
            "justificacionId", justificacion.getId().toString(),
            "aulaId", justificacion.getAulaId().toString(),
            "sesionId", request.sesionId().toString(),
            "estudianteId", request.estudianteId().toString()
        ));
        return justificacion;
    }

    @Transactional
    public Justificacion aprobar(UUID id) {
        String tenantId = TenantContext.require();
        Justificacion justificacion = justificaciones.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> ApiException.notFound("justificación no encontrada"));
        justificacion.setEstado("APROBADA");
        justificaciones.save(justificacion);
        registros.findByTenantIdAndSesionIdAndEstudianteId(tenantId, justificacion.getSesionId(), justificacion.getEstudianteId())
            .ifPresent(registro -> {
                registro.setEstado("JUSTIFICADO");
                registros.save(registro);
            });
        publicar(Topics.ATT_JUSTIFICACION, id.toString(), "JustificacionAprobada", Map.of(
            "justificacionId", id.toString(),
            "aulaId", justificacion.getAulaId().toString(),
            "sesionId", justificacion.getSesionId().toString(),
            "estudianteId", justificacion.getEstudianteId().toString()
        ));
        return justificacion;
    }

    @Transactional
    public Justificacion rechazar(UUID id) {
        String tenantId = TenantContext.require();
        Justificacion justificacion = justificaciones.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> ApiException.notFound("justificación no encontrada"));
        justificacion.setEstado("RECHAZADA");
        justificaciones.save(justificacion);
        publicar(Topics.ATT_JUSTIFICACION, id.toString(), "JustificacionRechazada", Map.of(
            "justificacionId", id.toString(),
            "aulaId", justificacion.getAulaId().toString(),
            "sesionId", justificacion.getSesionId().toString(),
            "estudianteId", justificacion.getEstudianteId().toString()
        ));
        return justificacion;
    }

    @Transactional(readOnly = true)
    public List<RegistroAsistencia> registros(UUID sesionId) {
        String tenantId = TenantContext.require();
        sesiones.findByIdAndTenantId(sesionId, tenantId)
            .orElseThrow(() -> ApiException.notFound("sesión no encontrada"));
        return registros.findByTenantIdAndSesionId(tenantId, sesionId);
    }

    @Transactional
    public void proyectarMatricula(UUID aulaId, UUID estudianteId) {
        String tenantId = TenantContext.require();
        if (matriculas.findByTenantIdAndAulaIdAndEstudianteId(tenantId, aulaId, estudianteId).isPresent()) {
            return;
        }
        MatriculaProyeccion proyeccion = new MatriculaProyeccion();
        proyeccion.setTenantId(tenantId);
        proyeccion.setAulaId(aulaId);
        proyeccion.setEstudianteId(estudianteId);
        matriculas.save(proyeccion);
    }

    @Transactional
    public void retirarMatricula(UUID aulaId, UUID estudianteId) {
        String tenantId = TenantContext.require();
        matriculas.findByTenantIdAndAulaIdAndEstudianteId(tenantId, aulaId, estudianteId)
            .ifPresent(matriculas::delete);
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "attendance-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
