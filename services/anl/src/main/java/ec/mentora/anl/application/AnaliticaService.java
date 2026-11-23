package ec.mentora.anl.application;

import ec.mentora.anl.domain.HechoAsistencia;
import ec.mentora.anl.domain.HechoAsistenciaRepository;
import ec.mentora.anl.domain.HechoEntrega;
import ec.mentora.anl.domain.HechoEntregaRepository;
import ec.mentora.anl.domain.HechoNota;
import ec.mentora.anl.domain.HechoNotaRepository;
import ec.mentora.anl.domain.HechoRiesgo;
import ec.mentora.anl.domain.HechoRiesgoRepository;
import ec.mentora.anl.domain.Tablero;
import ec.mentora.anl.domain.TableroRepository;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnaliticaService {

    private final HechoAsistenciaRepository asistencias;
    private final HechoNotaRepository notas;
    private final HechoEntregaRepository entregas;
    private final HechoRiesgoRepository riesgos;
    private final TableroRepository tableros;
    private final OutboxService outbox;

    @Transactional
    public List<Tablero> listarTableros() {
        String tenantId = TenantContext.require();
        List<Tablero> existentes = tableros.findByTenantId(tenantId);
        if (!existentes.isEmpty()) {
            return existentes;
        }
        crearTablero(tenantId, "Asistencia", "ASISTENCIA");
        crearTablero(tenantId, "Notas", "NOTAS");
        crearTablero(tenantId, "Entregas", "ENTREGAS");
        crearTablero(tenantId, "Riesgo", "RIESGO");
        return tableros.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> datos(UUID tableroId) {
        String tenantId = TenantContext.require();
        Tablero tablero = tableros.findByIdAndTenantId(tableroId, tenantId)
            .orElseThrow(() -> ApiException.notFound("tablero no encontrado"));
        return switch (tablero.getTipo()) {
            case "ASISTENCIA" -> Map.of(
                "total", asistencias.findByTenantId(tenantId).size(),
                "ausentes", asistencias.countByTenantIdAndEstado(tenantId, "AUSENTE")
            );
            case "NOTAS" -> Map.of("promedio", promedioNotas(tenantId));
            case "ENTREGAS" -> Map.of(
                "total", entregas.findByTenantId(tenantId).size(),
                "puntuales", entregas.countByTenantIdAndPuntual(tenantId, true)
            );
            case "RIESGO" -> Map.of("casos", riesgos.findByTenantId(tenantId).size());
            default -> Map.of("total", 0);
        };
    }

    @Transactional(readOnly = true)
    public Map<String, Object> metricas() {
        String tenantId = TenantContext.require();
        return Map.of(
            "asistencias", asistencias.findByTenantId(tenantId).size(),
            "notas", notas.findByTenantId(tenantId).size(),
            "entregas", entregas.findByTenantId(tenantId).size(),
            "riesgos", riesgos.findByTenantId(tenantId).size()
        );
    }

    @Transactional
    public void publicarMetrica() {
        publicar(Topics.ANL_TABLERO, "metricas", "MetricaRecalculada", metricas());
    }

    private BigDecimal promedioNotas(String tenantId) {
        List<HechoNota> registros = notas.findByTenantId(tenantId);
        double promedio = registros.stream().mapToDouble(nota -> nota.getValor().doubleValue()).average().orElse(0);
        return BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP);
    }

    private void crearTablero(String tenantId, String nombre, String tipo) {
        Tablero tablero = new Tablero();
        tablero.setTenantId(tenantId);
        tablero.setNombre(nombre);
        tablero.setTipo(tipo);
        tablero.setFiltros("{}");
        tableros.save(tablero);
    }

    private void publicar(String topic, String key, String type, Map<String, Object> data) {
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "analytics-service", type, key, TenantContext.require(), data
        );
        outbox.enqueue(topic, key, event);
    }
}
