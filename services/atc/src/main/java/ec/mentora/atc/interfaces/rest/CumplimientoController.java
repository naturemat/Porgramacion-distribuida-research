package ec.mentora.atc.interfaces.rest;

import ec.mentora.atc.application.CumplimientoService;
import ec.mentora.atc.application.dto.AtcDtos.CrearUmbralRequest;
import ec.mentora.atc.domain.Alerta;
import ec.mentora.atc.domain.Cumplimiento;
import ec.mentora.atc.domain.Umbral;
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
public class CumplimientoController {

    private final CumplimientoService service;

    @PostMapping("/umbrales")
    @ResponseStatus(HttpStatus.CREATED)
    public Umbral crearUmbral(@Valid @RequestBody CrearUmbralRequest request) {
        return service.crearUmbral(request);
    }

    @GetMapping("/cumplimiento")
    public List<Cumplimiento> listar(@RequestParam UUID aulaId) {
        return service.listar(aulaId);
    }

    @GetMapping("/alertas")
    public List<Alerta> alertas() {
        return service.alertas();
    }

    @PostMapping("/alertas/{id}/resolver")
    public Alerta resolver(@PathVariable UUID id) {
        return service.resolver(id);
    }
}
