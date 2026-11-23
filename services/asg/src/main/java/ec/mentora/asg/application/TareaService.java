package ec.mentora.asg.application;

import ec.mentora.asg.application.dto.AsgDtos.CalificarRequest;
import ec.mentora.asg.application.dto.AsgDtos.CrearTareaRequest;
import ec.mentora.asg.application.dto.AsgDtos.EntregaRequest;
import ec.mentora.asg.domain.CalificacionTarea;
import ec.mentora.asg.domain.CalificacionTareaRepository;
import ec.mentora.asg.domain.Entrega;
import ec.mentora.asg.domain.EntregaRepository;
import ec.mentora.asg.domain.Tarea;
import ec.mentora.asg.domain.TareaRepository;
import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.idempotency.IdempotencyService;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareas;
    private final EntregaRepository entregas;
    private final CalificacionTareaRepository calificaciones;
    private final IdempotencyService idempotency;
    private final OutboxService outbox;

    @Transactional
    public Tarea crear(CrearTareaRequest request) {
        Tarea tarea = new Tarea();
        tarea.setTenantId(TenantContext.require());
        tarea.setAulaId(request.aulaId());
        tarea.setUnidadId(request.unidadId());
        tarea.setPeriodoId(request.periodoId());
        tarea.setTitulo(request.titulo());
        tarea.setDescripcion(request.descripcion());
        tarea.setFechaLimite(request.fechaLimite());
        tarea.setPuntajeMaximo(request.puntajeMaximo());
        tarea.setEstado("PUBLICADA");
        tareas.save(tarea);
        publicar(Topics.ASG_TAREA, tarea.getId().toString(), "TareaPublicada", Map.of(
            "tareaId", tarea.getId().toString(),
            "aulaId", tarea.getAulaId().toString(),
            "periodoId", tarea.getPeriodoId().toString(),
            "titulo", tarea.getTitulo(),
            "fechaLimite", tarea.getFechaLimite().toString()
        ));
        return tarea;
    }

    @Transactional(readOnly = true)
    public List<Tarea> listar(UUID aulaId) {
        return tareas.findByTenantIdAndAulaIdOrderByCreatedAtDesc(TenantContext.require(), aulaId);
    }

    @Transactional
    public Entrega entregar(UUID tareaId, String key, EntregaRequest request) {
        return idempotency.execute(key, Entrega.class, () -> registrarEntrega(tareaId, request));
    }

    private Entrega registrarEntrega(UUID tareaId, EntregaRequest request) {
        String tenantId = TenantContext.require();
        Tarea tarea = tareas.findByIdAndTenantId(tareaId, tenantId)
            .orElseThrow(() -> ApiException.notFound("tarea no encontrada"));
        if ("CERRADA".equals(tarea.getEstado())) {
            throw ApiException.conflict("la tarea está cerrada");
        }
        Entrega entrega = entregas
            .findByTenantIdAndTareaIdAndEstudianteId(tenantId, tareaId, request.estudianteId())
            .orElseGet(Entrega::new);
        Instant ahora = Instant.now();
        entrega.setTenantId(tenantId);
        entrega.setTareaId(tareaId);
        entrega.setEstudianteId(request.estudianteId());
        entrega.setContenido(request.contenido());
        entrega.setArchivoId(request.archivoId());
        entrega.setEstado("RECIBIDA");
        entrega.setPuntual(!ahora.isAfter(tarea.getFechaLimite()));
        entrega.setRecibidaEn(ahora);
        entregas.save(entrega);
        publicar(Topics.ASG_ENTREGA, entrega.getId().toString(), "EntregaRecibida", Map.of(
            "entregaId", entrega.getId().toString(),
            "tareaId", tareaId.toString(),
            "aulaId", tarea.getAulaId().toString(),
            "periodoId", tarea.getPeriodoId().toString(),
            "estudianteId", request.estudianteId().toString(),
            "puntual", entrega.isPuntual()
        ));
        return entrega;
    }

    @Transactional
    public CalificacionTarea calificar(UUID entregaId, CalificarRequest request) {
        String tenantId = TenantContext.require();
        Entrega entrega = entregas.findByIdAndTenantId(entregaId, tenantId)
            .orElseThrow(() -> ApiException.notFound("entrega no encontrada"));
        Tarea tarea = tareas.findByIdAndTenantId(entrega.getTareaId(), tenantId)
            .orElseThrow(() -> ApiException.notFound("tarea no encontrada"));
        int version = calificaciones.findByTenantIdAndEntregaIdOrderByVersionDesc(tenantId, entregaId).size() + 1;
        CalificacionTarea calificacion = new CalificacionTarea();
        calificacion.setTenantId(tenantId);
        calificacion.setEntregaId(entregaId);
        calificacion.setPuntaje(request.puntaje());
        calificacion.setRetroalimentacion(request.retroalimentacion());
        calificacion.setVersion(version);
        calificaciones.save(calificacion);
        entrega.setEstado("CALIFICADA");
        entregas.save(entrega);
        publicar(Topics.ASG_ENTREGA, entregaId.toString(), "EntregaCalificada", Map.of(
            "entregaId", entregaId.toString(),
            "tareaId", tarea.getId().toString(),
            "aulaId", tarea.getAulaId().toString(),
            "periodoId", tarea.getPeriodoId().toString(),
            "estudianteId", entrega.getEstudianteId().toString(),
            "puntaje", request.puntaje(),
            "puntajeMaximo", tarea.getPuntajeMaximo()
        ));
        return calificacion;
    }

    @Transactional
    public Entrega devolver(UUID entregaId) {
        String tenantId = TenantContext.require();
        Entrega entrega = entregas.findByIdAndTenantId(entregaId, tenantId)
            .orElseThrow(() -> ApiException.notFound("entrega no encontrada"));
        entrega.setEstado("DEVUELTA");
        entregas.save(entrega);
        publicar(Topics.ASG_ENTREGA, entregaId.toString(), "EntregaDevuelta", Map.of(
            "entregaId", entregaId.toString(),
            "estudianteId", entrega.getEstudianteId().toString()
        ));
        return entrega;
    }

    @Transactional
    public void cerrarPorAula(UUID aulaId) {
        String tenantId = TenantContext.require();
        for (Tarea tarea : tareas.findByTenantIdAndAulaId(tenantId, aulaId)) {
            tarea.setEstado("CERRADA");
            tareas.save(tarea);
        }
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "assignment-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
