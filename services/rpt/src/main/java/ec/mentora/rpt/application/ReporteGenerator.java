package ec.mentora.rpt.application;

import ec.mentora.common.event.CloudEvent;
import ec.mentora.common.event.Topics;
import ec.mentora.common.outbox.OutboxService;
import ec.mentora.rpt.domain.AsistenciaProyeccion;
import ec.mentora.rpt.domain.AsistenciaProyeccionRepository;
import ec.mentora.rpt.domain.CasoProyeccion;
import ec.mentora.rpt.domain.CasoProyeccionRepository;
import ec.mentora.rpt.domain.NotaProyeccion;
import ec.mentora.rpt.domain.NotaProyeccionRepository;
import ec.mentora.rpt.domain.Reporte;
import ec.mentora.rpt.domain.ReporteRepository;
import ec.mentora.rpt.domain.VersionReporte;
import ec.mentora.rpt.domain.VersionReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReporteGenerator {

    private final ReporteRepository reportes;
    private final VersionReporteRepository versiones;
    private final NotaProyeccionRepository notas;
    private final AsistenciaProyeccionRepository asistencias;
    private final CasoProyeccionRepository casos;
    private final OutboxService outbox;

    @Async
    @Transactional
    public void generar(UUID reporteId) {
        Reporte reporte = reportes.findById(reporteId).orElse(null);
        if (reporte == null) {
            return;
        }
        reporte.setEstado("PROCESANDO");
        reportes.save(reporte);
        StringBuilder contenido = new StringBuilder("tipo,estudiante,valor\n");
        if ("NOTAS".equals(reporte.getTipo())) {
            for (NotaProyeccion nota : notas.findByTenantIdAndAulaIdAndPeriodoId(
                    reporte.getTenantId(), reporte.getAulaId(), reporte.getPeriodoId())) {
                contenido.append("NOTA,").append(nota.getEstudianteId()).append(",").append(nota.getValor()).append("\n");
            }
        } else if ("ASISTENCIA".equals(reporte.getTipo())) {
            for (AsistenciaProyeccion asistencia : asistencias.findByTenantIdAndAulaId(
                    reporte.getTenantId(), reporte.getAulaId())) {
                contenido.append("ASISTENCIA,").append(asistencia.getEstudianteId()).append(",").append(asistencia.getFaltas()).append("\n");
            }
        } else {
            for (CasoProyeccion caso : casos.findByTenantId(reporte.getTenantId())) {
                contenido.append("RIESGO,").append(caso.getEstudianteId()).append(",").append(caso.getPuntaje()).append("\n");
            }
        }
        reporte.setContenido(contenido.toString());
        reporte.setEstado("GENERADO");
        reporte.setGeneradoEn(Instant.now());
        reportes.save(reporte);
        VersionReporte version = new VersionReporte();
        version.setTenantId(reporte.getTenantId());
        version.setReporteId(reporte.getId());
        version.setVersion(1);
        version.setContenido(contenido.toString());
        versiones.save(version);
        CloudEvent<Map<String, Object>> event = CloudEvent.of(
            "reporting-service", "ReporteGenerado", reporteId.toString(), reporte.getTenantId(),
            Map.of("reporteId", reporteId.toString(), "tipo", reporte.getTipo())
        );
        outbox.enqueue(Topics.RPT_REPORTE, reporteId.toString(), event);
    }
}
