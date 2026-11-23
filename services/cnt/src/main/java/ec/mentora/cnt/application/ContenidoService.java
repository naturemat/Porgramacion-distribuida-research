package ec.mentora.cnt.application;

import ec.mentora.cnt.application.dto.CntDtos.CrearContenidoRequest;
import ec.mentora.cnt.application.dto.CntDtos.CrearVersionRequest;
import ec.mentora.cnt.application.dto.CntDtos.EnlaceArchivoRequest;
import ec.mentora.cnt.application.dto.CntDtos.EnlaceResponse;
import ec.mentora.cnt.domain.Contenido;
import ec.mentora.cnt.domain.ContenidoRepository;
import ec.mentora.cnt.domain.ReferenciaArchivo;
import ec.mentora.cnt.domain.ReferenciaArchivoRepository;
import ec.mentora.cnt.domain.VersionContenido;
import ec.mentora.cnt.domain.VersionContenidoRepository;
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
public class ContenidoService {

    private final ContenidoRepository contenidos;
    private final VersionContenidoRepository versiones;
    private final ReferenciaArchivoRepository archivos;
    private final OutboxService outbox;

    @Transactional
    public Contenido crear(CrearContenidoRequest request) {
        Contenido contenido = new Contenido();
        contenido.setTenantId(TenantContext.require());
        contenido.setAulaId(request.aulaId());
        contenido.setUnidadId(request.unidadId());
        contenido.setTitulo(request.titulo());
        contenido.setEstado("BORRADOR");
        return contenidos.save(contenido);
    }

    @Transactional(readOnly = true)
    public List<Contenido> listar(UUID aulaId) {
        return contenidos.findByTenantIdAndAulaIdOrderByCreatedAtDesc(TenantContext.require(), aulaId);
    }

    @Transactional
    public VersionContenido crearVersion(UUID contenidoId, CrearVersionRequest request) {
        String tenantId = TenantContext.require();
        Contenido contenido = contenidos.findByIdAndTenantId(contenidoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("contenido no encontrado"));
        int numero = (int) versiones.countByTenantIdAndContenidoId(tenantId, contenidoId) + 1;
        VersionContenido version = new VersionContenido();
        version.setTenantId(tenantId);
        version.setContenidoId(contenidoId);
        version.setNumero(numero);
        version.setCuerpo(request.cuerpo());
        version.setArchivoId(request.archivoId());
        version.setEstado("BORRADOR");
        versiones.save(version);
        publicar(Topics.CNT_CONTENIDO, contenidoId.toString(), "ContenidoVersionado", Map.of(
            "contenidoId", contenidoId.toString(),
            "aulaId", contenido.getAulaId().toString(),
            "versionId", version.getId().toString(),
            "numero", numero
        ));
        return version;
    }

    @Transactional
    public Contenido publicar(UUID contenidoId) {
        String tenantId = TenantContext.require();
        Contenido contenido = contenidos.findByIdAndTenantId(contenidoId, tenantId)
            .orElseThrow(() -> ApiException.notFound("contenido no encontrado"));
        contenido.setEstado("PUBLICADO");
        contenidos.save(contenido);
        publicar(Topics.CNT_CONTENIDO, contenidoId.toString(), "ContenidoPublicado", Map.of(
            "contenidoId", contenidoId.toString(),
            "aulaId", contenido.getAulaId().toString(),
            "unidadId", contenido.getUnidadId() == null ? "" : contenido.getUnidadId().toString(),
            "titulo", contenido.getTitulo()
        ));
        return contenido;
    }

    @Transactional
    public EnlaceResponse enlace(EnlaceArchivoRequest request) {
        String tenantId = TenantContext.require();
        Contenido contenido = contenidos.findByIdAndTenantId(request.contenidoId(), tenantId)
            .orElseThrow(() -> ApiException.notFound("contenido no encontrado"));
        ReferenciaArchivo referencia = archivos.findByTenantIdAndArchivoId(tenantId, request.archivoId())
            .orElseGet(ReferenciaArchivo::new);
        referencia.setTenantId(tenantId);
        referencia.setContenidoId(contenido.getId());
        referencia.setArchivoId(request.archivoId());
        referencia.setNombre(request.nombre());
        referencia.setTipoMime(request.tipoMime());
        referencia.setTamano(request.tamano());
        referencia.setEstado("PENDIENTE");
        archivos.save(referencia);
        return new EnlaceResponse(request.archivoId(), "https://s3.local/upload/" + request.archivoId());
    }

    @Transactional
    public void onVeredicto(UUID archivoId, String veredicto) {
        String tenantId = TenantContext.require();
        archivos.findByTenantIdAndArchivoId(tenantId, archivoId).ifPresent(referencia -> {
            referencia.setEstado(veredicto);
            archivos.save(referencia);
            if ("RECHAZADO".equals(veredicto)) {
                publicar(Topics.CNT_CONTENIDO, referencia.getContenidoId().toString(), "ContenidoEnCuarentena", Map.of(
                    "contenidoId", referencia.getContenidoId().toString(),
                    "archivoId", archivoId.toString()
                ));
            }
        });
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "content-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
