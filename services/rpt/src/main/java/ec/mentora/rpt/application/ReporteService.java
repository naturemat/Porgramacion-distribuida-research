package ec.mentora.rpt.application;

import ec.mentora.common.tenant.TenantContext;
import ec.mentora.common.web.ApiException;
import ec.mentora.rpt.application.dto.RptDtos.SolicitarReporteRequest;
import ec.mentora.rpt.domain.AsistenciaProyeccion;
import ec.mentora.rpt.domain.AsistenciaProyeccionRepository;
import ec.mentora.rpt.domain.CierreProyeccionRepository;
import ec.mentora.rpt.domain.NotaProyeccion;
import ec.mentora.rpt.domain.NotaProyeccionRepository;
import ec.mentora.rpt.domain.Reporte;
import ec.mentora.rpt.domain.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reportes;
    private final NotaProyeccionRepository notas;
    private final AsistenciaProyeccionRepository asistencias;
    private final CierreProyeccionRepository cierres;
    private final ReporteGenerator generador;

    public Reporte solicitar(SolicitarReporteRequest request) {
        Reporte reporte = new Reporte();
        reporte.setTenantId(TenantContext.require());
        reporte.setTipo(request.tipo());
        reporte.setAulaId(request.aulaId());
        reporte.setPeriodoId(request.periodoId());
        reporte.setEstado("SOLICITADO");
        reportes.save(reporte);
        generador.generar(reporte.getId());
        return reporte;
    }

    @Transactional(readOnly = true)
    public Reporte obtener(UUID id) {
        return reportes.findByIdAndTenantId(id, TenantContext.require())
            .orElseThrow(() -> ApiException.notFound("reporte no encontrado"));
    }

    @Transactional(readOnly = true)
    public String descargar(UUID id) {
        Reporte reporte = obtener(id);
        if (!"GENERADO".equals(reporte.getEstado())) {
            throw ApiException.conflict("el reporte aún no está generado");
        }
        return reporte.getContenido();
    }

    @Transactional(readOnly = true)
    public String libreta(UUID aulaId, UUID periodoId) {
        String tenantId = TenantContext.require();
        cierres.findByTenantIdAndAulaIdAndPeriodoId(tenantId, aulaId, periodoId)
            .orElseThrow(() -> ApiException.conflict("el periodo no está cerrado"));
        List<NotaProyeccion> notasAula = notas.findByTenantIdAndAulaIdAndPeriodoId(tenantId, aulaId, periodoId);
        List<AsistenciaProyeccion> asistenciasAula = asistencias.findByTenantIdAndAulaId(tenantId, aulaId);
        StringBuilder contenido = new StringBuilder("tipo,estudiante,valor\n");
        for (NotaProyeccion nota : notasAula) {
            contenido.append("NOTA,").append(nota.getEstudianteId()).append(",").append(nota.getValor()).append("\n");
        }
        for (AsistenciaProyeccion asistencia : asistenciasAula) {
            contenido.append("ASISTENCIA,").append(asistencia.getEstudianteId()).append(",").append(asistencia.getFaltas()).append("\n");
        }
        return contenido.toString();
    }
}
