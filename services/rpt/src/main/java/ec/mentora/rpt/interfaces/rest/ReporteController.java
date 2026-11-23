package ec.mentora.rpt.interfaces.rest;

import ec.mentora.rpt.application.ReporteService;
import ec.mentora.rpt.application.dto.RptDtos.SolicitarReporteRequest;
import ec.mentora.rpt.domain.Reporte;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @PostMapping("/reportes")
    public ResponseEntity<Reporte> solicitar(@Valid @RequestBody SolicitarReporteRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.solicitar(request));
    }

    @GetMapping("/reportes/{id}")
    public Reporte obtener(@PathVariable UUID id) {
        return service.obtener(id);
    }

    @GetMapping("/reportes/{id}/descarga")
    public String descargar(@PathVariable UUID id) {
        return service.descargar(id);
    }

    @GetMapping("/libretas")
    public String libreta(@RequestParam UUID aulaId, @RequestParam UUID periodoId) {
        return service.libreta(aulaId, periodoId);
    }
}
