package ec.mentora.asg.interfaces.rest;

import ec.mentora.asg.application.TareaService;
import ec.mentora.asg.application.dto.AsgDtos.CalificarRequest;
import ec.mentora.asg.application.dto.AsgDtos.CrearTareaRequest;
import ec.mentora.asg.application.dto.AsgDtos.EntregaRequest;
import ec.mentora.asg.domain.CalificacionTarea;
import ec.mentora.asg.domain.Entrega;
import ec.mentora.asg.domain.Tarea;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService service;

    @PostMapping("/tareas")
    @ResponseStatus(HttpStatus.CREATED)
    public Tarea crear(@Valid @RequestBody CrearTareaRequest request) {
        return service.crear(request);
    }

    @GetMapping("/tareas")
    public List<Tarea> listar(@RequestParam UUID aulaId) {
        return service.listar(aulaId);
    }

    @PostMapping("/tareas/{id}/entregas")
    @ResponseStatus(HttpStatus.CREATED)
    public Entrega entregar(
        @PathVariable UUID id,
        @RequestHeader("Idempotency-Key") String key,
        @Valid @RequestBody EntregaRequest request
    ) {
        return service.entregar(id, key, request);
    }

    @PostMapping("/entregas/{id}/calificacion")
    @ResponseStatus(HttpStatus.CREATED)
    public CalificacionTarea calificar(@PathVariable UUID id, @Valid @RequestBody CalificarRequest request) {
        return service.calificar(id, request);
    }

    @PostMapping("/entregas/{id}/devolver")
    public Entrega devolver(@PathVariable UUID id) {
        return service.devolver(id);
    }
}
