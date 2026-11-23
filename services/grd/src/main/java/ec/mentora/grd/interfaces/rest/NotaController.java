package ec.mentora.grd.interfaces.rest;

import ec.mentora.grd.application.NotaService;
import ec.mentora.grd.application.dto.GrdDtos.CierreRequest;
import ec.mentora.grd.application.dto.GrdDtos.CorreccionRequest;
import ec.mentora.grd.application.dto.GrdDtos.CrearComponenteRequest;
import ec.mentora.grd.application.dto.GrdDtos.NotaLoteRequest;
import ec.mentora.grd.application.dto.GrdDtos.RegistrarNotaRequest;
import ec.mentora.grd.domain.CierrePeriodo;
import ec.mentora.grd.domain.ComponenteEvaluacion;
import ec.mentora.grd.domain.Nota;
import ec.mentora.grd.domain.Promedio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService service;

    @PostMapping("/componentes")
    @ResponseStatus(HttpStatus.CREATED)
    public ComponenteEvaluacion crearComponente(@Valid @RequestBody CrearComponenteRequest request) {
        return service.crearComponente(request);
    }

    @PostMapping("/notas")
    @ResponseStatus(HttpStatus.CREATED)
    public Nota registrar(@Valid @RequestBody RegistrarNotaRequest request) {
        return service.registrar(request);
    }

    @PostMapping("/notas/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Nota> registrarLote(@Valid @RequestBody NotaLoteRequest request) {
        return service.registrarLote(request);
    }

    @PostMapping("/notas/{id}/correcciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Nota corregir(@PathVariable UUID id, @Valid @RequestBody CorreccionRequest request) {
        return service.corregir(id, request);
    }

    @PostMapping("/cierres")
    @ResponseStatus(HttpStatus.CREATED)
    public CierrePeriodo cerrar(@Valid @RequestBody CierreRequest request) {
        return service.cerrar(request);
    }

    @GetMapping("/aulas/{id}/promedios")
    public List<Promedio> promedios(@PathVariable UUID id, @RequestParam UUID periodoId) {
        return service.promedios(id, periodoId);
    }
}
