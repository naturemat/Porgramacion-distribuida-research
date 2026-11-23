package ec.mentora.aul.application;

import ec.mentora.aul.application.dto.AulDtos.ActividadRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearAnuncioRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearAulaRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearMatriculaRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearUnidadRequest;
import ec.mentora.aul.domain.ActividadAula;
import ec.mentora.aul.domain.ActividadAulaRepository;
import ec.mentora.aul.domain.Anuncio;
import ec.mentora.aul.domain.AnuncioRepository;
import ec.mentora.aul.domain.Aula;
import ec.mentora.aul.domain.AulaRepository;
import ec.mentora.aul.domain.MatriculaAula;
import ec.mentora.aul.domain.MatriculaAulaRepository;
import ec.mentora.aul.domain.Unidad;
import ec.mentora.aul.domain.UnidadRepository;
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
public class AulaService {

    private final AulaRepository aulas;
    private final MatriculaAulaRepository matriculas;
    private final UnidadRepository unidades;
    private final AnuncioRepository anuncios;
    private final ActividadAulaRepository actividades;
    private final OutboxService outbox;

    @Transactional
    public Aula crear(CrearAulaRequest request) {
        Aula aula = new Aula();
        aula.setTenantId(TenantContext.require());
        aula.setNombre(request.nombre());
        aula.setPeriodoId(request.periodoId());
        aula.setEstado("ACTIVA");
        aula.setFechaInicio(request.fechaInicio());
        aula.setFechaFin(request.fechaFin());
        aulas.save(aula);
        publicar(Topics.AUL_AULA, aula.getId().toString(), "AulaCreada", Map.of(
            "aulaId", aula.getId().toString(),
            "nombre", aula.getNombre(),
            "periodoId", aula.getPeriodoId().toString()
        ));
        return aula;
    }

    @Transactional(readOnly = true)
    public List<Aula> listar() {
        return aulas.findByTenantIdOrderByCreatedAtDesc(TenantContext.require());
    }

    @Transactional(readOnly = true)
    public Aula obtener(UUID id) {
        return aulas.findByIdAndTenantId(id, TenantContext.require())
            .orElseThrow(() -> ApiException.notFound("aula no encontrada"));
    }

    @Transactional
    public MatriculaAula matricular(UUID aulaId, CrearMatriculaRequest request) {
        Aula aula = obtener(aulaId);
        if ("CERRADA".equals(aula.getEstado())) {
            throw ApiException.conflict("el aula está cerrada");
        }
        MatriculaAula matricula = matriculas
            .findByTenantIdAndAulaIdAndEstudianteId(aula.getTenantId(), aulaId, request.estudianteId())
            .orElseGet(MatriculaAula::new);
        matricula.setTenantId(aula.getTenantId());
        matricula.setAulaId(aulaId);
        matricula.setEstudianteId(request.estudianteId());
        matricula.setEstado("ACTIVA");
        matriculas.save(matricula);
        publicar(Topics.AUL_AULA, aulaId.toString(), "EstudianteMatriculado", Map.of(
            "aulaId", aulaId.toString(),
            "estudianteId", request.estudianteId().toString(),
            "periodoId", aula.getPeriodoId().toString()
        ));
        return matricula;
    }

    @Transactional
    public void retirar(UUID aulaId, UUID estudianteId) {
        Aula aula = obtener(aulaId);
        MatriculaAula matricula = matriculas
            .findByTenantIdAndAulaIdAndEstudianteId(aula.getTenantId(), aulaId, estudianteId)
            .orElseThrow(() -> ApiException.notFound("matrícula no encontrada"));
        matricula.setEstado("RETIRADA");
        matriculas.save(matricula);
        publicar(Topics.AUL_AULA, aulaId.toString(), "EstudianteRetirado", Map.of(
            "aulaId", aulaId.toString(),
            "estudianteId", estudianteId.toString(),
            "periodoId", aula.getPeriodoId().toString()
        ));
    }

    @Transactional
    public Unidad crearUnidad(UUID aulaId, CrearUnidadRequest request) {
        Aula aula = obtener(aulaId);
        Unidad unidad = new Unidad();
        unidad.setTenantId(aula.getTenantId());
        unidad.setAulaId(aulaId);
        unidad.setTitulo(request.titulo());
        unidad.setOrden(request.orden());
        unidad.setEstado("BORRADOR");
        return unidades.save(unidad);
    }

    @Transactional
    public Unidad publicarUnidad(UUID aulaId, UUID unidadId) {
        Aula aula = obtener(aulaId);
        Unidad unidad = unidades.findByIdAndTenantId(unidadId, aula.getTenantId())
            .orElseThrow(() -> ApiException.notFound("unidad no encontrada"));
        unidad.setEstado("PUBLICADA");
        unidades.save(unidad);
        publicar(Topics.AUL_UNIDAD, aulaId.toString(), "UnidadPublicada", Map.of(
            "aulaId", aulaId.toString(),
            "unidadId", unidadId.toString(),
            "titulo", unidad.getTitulo(),
            "periodoId", aula.getPeriodoId().toString()
        ));
        return unidad;
    }

    @Transactional
    public Anuncio crearAnuncio(UUID aulaId, CrearAnuncioRequest request) {
        Aula aula = obtener(aulaId);
        Anuncio anuncio = new Anuncio();
        anuncio.setTenantId(aula.getTenantId());
        anuncio.setAulaId(aulaId);
        anuncio.setTitulo(request.titulo());
        anuncio.setContenido(request.contenido());
        anuncios.save(anuncio);
        publicar(Topics.AUL_ANUNCIO, aulaId.toString(), "AnuncioPublicado", Map.of(
            "aulaId", aulaId.toString(),
            "anuncioId", anuncio.getId().toString(),
            "titulo", anuncio.getTitulo()
        ));
        return anuncio;
    }

    @Transactional
    public ActividadAula registrarActividad(UUID aulaId, ActividadRequest request) {
        Aula aula = obtener(aulaId);
        ActividadAula actividad = new ActividadAula();
        actividad.setTenantId(aula.getTenantId());
        actividad.setAulaId(aulaId);
        actividad.setHora(request.hora());
        actividad.setContadores(request.contadores());
        actividades.save(actividad);
        publicar(Topics.AUL_ACTIVIDAD, aulaId.toString(), "ActividadRegistrada", Map.of(
            "aulaId", aulaId.toString(),
            "hora", request.hora().toString(),
            "contadores", request.contadores()
        ));
        return actividad;
    }

    @Transactional
    public Aula cerrar(UUID aulaId) {
        Aula aula = obtener(aulaId);
        aula.setEstado("CERRADA");
        aulas.save(aula);
        publicar(Topics.AUL_AULA, aulaId.toString(), "AulaCerrada", Map.of(
            "aulaId", aulaId.toString(),
            "periodoId", aula.getPeriodoId().toString()
        ));
        return aula;
    }

    @Transactional
    public void cerrarPorPeriodo(UUID periodoId) {
        String tenantId = TenantContext.require();
        for (Aula aula : aulas.findByTenantIdAndPeriodoId(tenantId, periodoId)) {
            if (!"CERRADA".equals(aula.getEstado())) {
                aula.setEstado("CERRADA");
                aulas.save(aula);
                publicar(Topics.AUL_AULA, aula.getId().toString(), "AulaCerrada", Map.of(
                    "aulaId", aula.getId().toString(),
                    "periodoId", periodoId.toString()
                ));
            }
        }
    }

    @Transactional
    public void crearDesdeDistributivo(UUID aulaId, String nombre, UUID periodoId) {
        String tenantId = TenantContext.require();
        if (aulas.findByIdAndTenantId(aulaId, tenantId).isPresent()) {
            return;
        }
        Aula aula = new Aula();
        aula.setId(aulaId);
        aula.setTenantId(tenantId);
        aula.setNombre(nombre);
        aula.setPeriodoId(periodoId);
        aula.setEstado("ACTIVA");
        aulas.save(aula);
        publicar(Topics.AUL_AULA, aulaId.toString(), "AulaCreada", Map.of(
            "aulaId", aulaId.toString(),
            "nombre", nombre,
            "periodoId", periodoId.toString()
        ));
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "classroom-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
