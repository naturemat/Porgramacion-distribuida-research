package ec.mentora.cue.application;

import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import ec.mentora.cue.application.dto.CueDtos.CrearCuestionarioRequest;
import ec.mentora.cue.application.dto.CueDtos.OpcionRequest;
import ec.mentora.cue.application.dto.CueDtos.PreguntaRequest;
import ec.mentora.cue.application.dto.CueDtos.ResponderRequest;
import ec.mentora.cue.domain.Cuestionario;
import ec.mentora.cue.domain.CuestionarioRepository;
import ec.mentora.cue.domain.Intento;
import ec.mentora.cue.domain.IntentoRepository;
import ec.mentora.cue.domain.Opcion;
import ec.mentora.cue.domain.OpcionRepository;
import ec.mentora.cue.domain.Pregunta;
import ec.mentora.cue.domain.PreguntaRepository;
import ec.mentora.cue.domain.Respuesta;
import ec.mentora.cue.domain.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CuestionarioService {

    private final CuestionarioRepository cuestionarios;
    private final PreguntaRepository preguntas;
    private final OpcionRepository opciones;
    private final IntentoRepository intentos;
    private final RespuestaRepository respuestas;
    private final OutboxService outbox;

    @Transactional
    public Cuestionario crear(CrearCuestionarioRequest request) {
        String tenantId = TenantContext.require();
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setTenantId(tenantId);
        cuestionario.setAulaId(request.aulaId());
        cuestionario.setUnidadId(request.unidadId());
        cuestionario.setPeriodoId(request.periodoId());
        cuestionario.setTitulo(request.titulo());
        cuestionario.setEstado("BORRADOR");
        cuestionario.setIntentosMaximos(request.intentosMaximos());
        cuestionarios.save(cuestionario);
        if (request.preguntas() != null) {
            for (PreguntaRequest preguntaRequest : request.preguntas()) {
                Pregunta pregunta = new Pregunta();
                pregunta.setTenantId(tenantId);
                pregunta.setCuestionarioId(cuestionario.getId());
                pregunta.setEnunciado(preguntaRequest.enunciado());
                pregunta.setTipo(preguntaRequest.tipo());
                pregunta.setPuntaje(preguntaRequest.puntaje());
                preguntas.save(pregunta);
                if (preguntaRequest.opciones() != null) {
                    for (OpcionRequest opcionRequest : preguntaRequest.opciones()) {
                        Opcion opcion = new Opcion();
                        opcion.setTenantId(tenantId);
                        opcion.setPreguntaId(pregunta.getId());
                        opcion.setTexto(opcionRequest.texto());
                        opcion.setCorrecta(opcionRequest.correcta());
                        opciones.save(opcion);
                    }
                }
            }
        }
        return cuestionario;
    }

    @Transactional(readOnly = true)
    public List<Cuestionario> listar(UUID aulaId) {
        return cuestionarios.findByTenantIdAndAulaIdOrderByCreatedAtDesc(TenantContext.require(), aulaId);
    }

    @Transactional
    public Cuestionario publicar(UUID id) {
        String tenantId = TenantContext.require();
        Cuestionario cuestionario = cuestionarios.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> ApiException.notFound("cuestionario no encontrado"));
        cuestionario.setEstado("PUBLICADO");
        cuestionarios.save(cuestionario);
        publicar(Topics.CUE_CUESTIONARIO, id.toString(), "CuestionarioPublicado", Map.of(
            "cuestionarioId", id.toString(),
            "aulaId", cuestionario.getAulaId().toString(),
            "periodoId", cuestionario.getPeriodoId().toString(),
            "titulo", cuestionario.getTitulo()
        ));
        return cuestionario;
    }

    @Transactional
    public Intento iniciar(UUID cuestionarioId, UUID estudianteId) {
        String tenantId = TenantContext.require();
        Cuestionario cuestionario = cuestionarios.findByIdAndTenantId(cuestionarioId, tenantId)
            .orElseThrow(() -> ApiException.notFound("cuestionario no encontrado"));
        if (!"PUBLICADO".equals(cuestionario.getEstado())) {
            throw ApiException.conflict("el cuestionario no está publicado");
        }
        long usados = intentos.countByTenantIdAndCuestionarioIdAndEstudianteId(tenantId, cuestionarioId, estudianteId);
        if (usados >= cuestionario.getIntentosMaximos()) {
            throw ApiException.conflict("se agotaron los intentos");
        }
        Intento intento = new Intento();
        intento.setTenantId(tenantId);
        intento.setCuestionarioId(cuestionarioId);
        intento.setEstudianteId(estudianteId);
        intento.setEstado("INICIADO");
        intento.setIniciadoEn(Instant.now());
        intentos.save(intento);
        publicar(Topics.CUE_INTENTO, intento.getId().toString(), "IntentoIniciado", Map.of(
            "intentoId", intento.getId().toString(),
            "cuestionarioId", cuestionarioId.toString(),
            "aulaId", cuestionario.getAulaId().toString(),
            "periodoId", cuestionario.getPeriodoId().toString(),
            "estudianteId", estudianteId.toString()
        ));
        return intento;
    }

    @Transactional
    public Respuesta responder(UUID intentoId, ResponderRequest request) {
        String tenantId = TenantContext.require();
        Intento intento = intentos.findByIdAndTenantId(intentoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("intento no encontrado"));
        if (!"INICIADO".equals(intento.getEstado())) {
            throw ApiException.conflict("el intento no está abierto");
        }
        Respuesta respuesta = respuestas
            .findByTenantIdAndIntentoIdAndPreguntaId(tenantId, intentoId, request.preguntaId())
            .orElseGet(Respuesta::new);
        respuesta.setTenantId(tenantId);
        respuesta.setIntentoId(intentoId);
        respuesta.setPreguntaId(request.preguntaId());
        respuesta.setOpcionId(request.opcionId());
        respuesta.setTexto(request.texto());
        return respuestas.save(respuesta);
    }

    @Transactional
    public Intento enviar(UUID intentoId) {
        String tenantId = TenantContext.require();
        Intento intento = intentos.findByIdAndTenantId(intentoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("intento no encontrado"));
        if (!"INICIADO".equals(intento.getEstado())) {
            throw ApiException.conflict("el intento ya fue enviado");
        }
        Cuestionario cuestionario = cuestionarios.findByIdAndTenantId(intento.getCuestionarioId(), tenantId)
            .orElseThrow(() -> ApiException.notFound("cuestionario no encontrado"));
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal maximo = BigDecimal.ZERO;
        for (Pregunta pregunta : preguntas.findByTenantIdAndCuestionarioId(tenantId, cuestionario.getId())) {
            maximo = maximo.add(pregunta.getPuntaje());
            Respuesta respuesta = respuestas
                .findByTenantIdAndIntentoIdAndPreguntaId(tenantId, intentoId, pregunta.getId())
                .orElse(null);
            if (respuesta != null && respuesta.getOpcionId() != null) {
                Opcion opcion = opciones.findByIdAndTenantId(respuesta.getOpcionId(), tenantId).orElse(null);
                if (opcion != null && opcion.isCorrecta()) {
                    total = total.add(pregunta.getPuntaje());
                }
            }
        }
        intento.setEstado("CALIFICADO");
        intento.setEnviadoEn(Instant.now());
        intento.setPuntaje(total);
        intentos.save(intento);
        publicar(Topics.CUE_INTENTO, intentoId.toString(), "IntentoEnviado", Map.of(
            "intentoId", intentoId.toString(),
            "cuestionarioId", cuestionario.getId().toString(),
            "aulaId", cuestionario.getAulaId().toString(),
            "periodoId", cuestionario.getPeriodoId().toString(),
            "estudianteId", intento.getEstudianteId().toString()
        ));
        publicar(Topics.CUE_INTENTO, intentoId.toString(), "IntentoCalificado", Map.of(
            "intentoId", intentoId.toString(),
            "cuestionarioId", cuestionario.getId().toString(),
            "aulaId", cuestionario.getAulaId().toString(),
            "periodoId", cuestionario.getPeriodoId().toString(),
            "estudianteId", intento.getEstudianteId().toString(),
            "puntaje", total,
            "puntajeMaximo", maximo
        ));
        return intento;
    }

    @Transactional
    public void cerrarPorAula(UUID aulaId) {
        String tenantId = TenantContext.require();
        for (Cuestionario cuestionario : cuestionarios.findByTenantIdAndAulaId(tenantId, aulaId)) {
            cuestionario.setEstado("CERRADO");
            cuestionarios.save(cuestionario);
        }
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "quiz-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
