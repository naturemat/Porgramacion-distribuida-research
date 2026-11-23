package ec.mentora.rsk.interfaces.rest;

import ec.mentora.rsk.application.RiesgoService;
import ec.mentora.rsk.application.dto.RskDtos.IntervencionRequest;
import ec.mentora.rsk.application.dto.RskDtos.RevisionRequest;
import ec.mentora.rsk.domain.CasoRiesgo;
import ec.mentora.rsk.domain.Intervencion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/casos")
@RequiredArgsConstructor
public class RiesgoController {

    private final RiesgoService service;

    @GetMapping
    public List<CasoRiesgo> listar() {
        return service.listarCasos();
    }

    @PostMapping("/{id}/revision")
    @ResponseStatus(HttpStatus.CREATED)
    public CasoRiesgo revisar(@PathVariable UUID id, @Valid @RequestBody RevisionRequest request) {
        return service.revisar(id, request);
    }

    @PostMapping("/{id}/intervenciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Intervencion intervenir(@PathVariable UUID id, @Valid @RequestBody IntervencionRequest request) {
        return service.intervenir(id, request);
    }

    @PostMapping("/{id}/escalar")
    public CasoRiesgo escalar(@PathVariable UUID id) {
        return service.escalar(id);
    }

    @PostMapping("/{id}/cerrar")
    public CasoRiesgo cerrar(@PathVariable UUID id) {
        return service.cerrar(id);
    }
}
