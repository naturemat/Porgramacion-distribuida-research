package ec.mentora.cue.interfaces.rest;

import ec.mentora.cue.application.CuestionarioService;
import ec.mentora.cue.application.dto.CueDtos.CrearCuestionarioRequest;
import ec.mentora.cue.application.dto.CueDtos.IniciarIntentoRequest;
import ec.mentora.cue.application.dto.CueDtos.ResponderRequest;
import ec.mentora.cue.domain.Cuestionario;
import ec.mentora.cue.domain.Intento;
import ec.mentora.cue.domain.Respuesta;
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
public class CuestionarioController {

    private final CuestionarioService service;

    @PostMapping("/cuestionarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Cuestionario crear(@Valid @RequestBody CrearCuestionarioRequest request) {
        return service.crear(request);
    }

    @GetMapping("/cuestionarios")
    public List<Cuestionario> listar(@RequestParam UUID aulaId) {
        return service.listar(aulaId);
    }

    @PostMapping("/cuestionarios/{id}/publicar")
    public Cuestionario publicar(@PathVariable UUID id) {
        return service.publicar(id);
    }

    @PostMapping("/cuestionarios/{id}/intentos")
    @ResponseStatus(HttpStatus.CREATED)
    public Intento iniciar(@PathVariable UUID id, @Valid @RequestBody IniciarIntentoRequest request) {
        return service.iniciar(id, request.estudianteId());
    }

    @PostMapping("/intentos/{id}/respuestas")
    @ResponseStatus(HttpStatus.CREATED)
    public Respuesta responder(@PathVariable UUID id, @Valid @RequestBody ResponderRequest request) {
        return service.responder(id, request);
    }

    @PostMapping("/intentos/{id}/enviar")
    public Intento enviar(@PathVariable UUID id) {
        return service.enviar(id);
    }
}
