package ec.mentora.aul.interfaces.rest;

import ec.mentora.aul.application.AulaService;
import ec.mentora.aul.application.dto.AulDtos.ActividadRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearAnuncioRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearAulaRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearMatriculaRequest;
import ec.mentora.aul.application.dto.AulDtos.CrearUnidadRequest;
import ec.mentora.aul.domain.ActividadAula;
import ec.mentora.aul.domain.Anuncio;
import ec.mentora.aul.domain.Aula;
import ec.mentora.aul.domain.MatriculaAula;
import ec.mentora.aul.domain.Unidad;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/v1/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aula crear(@Valid @RequestBody CrearAulaRequest request) {
        return service.crear(request);
    }

    @GetMapping
    public List<Aula> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Aula obtener(@PathVariable UUID id) {
        return service.obtener(id);
    }

    @PostMapping("/{id}/matriculas")
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculaAula matricular(@PathVariable UUID id, @Valid @RequestBody CrearMatriculaRequest request) {
        return service.matricular(id, request);
    }

    @DeleteMapping("/{id}/matriculas/{estudianteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirar(@PathVariable UUID id, @PathVariable UUID estudianteId) {
        service.retirar(id, estudianteId);
    }

    @PostMapping("/{id}/unidades")
    @ResponseStatus(HttpStatus.CREATED)
    public Unidad crearUnidad(@PathVariable UUID id, @Valid @RequestBody CrearUnidadRequest request) {
        return service.crearUnidad(id, request);
    }

    @PostMapping("/{id}/unidades/{unidadId}/publicar")
    public Unidad publicarUnidad(@PathVariable UUID id, @PathVariable UUID unidadId) {
        return service.publicarUnidad(id, unidadId);
    }

    @PostMapping("/{id}/anuncios")
    @ResponseStatus(HttpStatus.CREATED)
    public Anuncio crearAnuncio(@PathVariable UUID id, @Valid @RequestBody CrearAnuncioRequest request) {
        return service.crearAnuncio(id, request);
    }

    @PostMapping("/{id}/actividad")
    @ResponseStatus(HttpStatus.CREATED)
    public ActividadAula registrarActividad(@PathVariable UUID id, @Valid @RequestBody ActividadRequest request) {
        return service.registrarActividad(id, request);
    }

    @PostMapping("/{id}/cerrar")
    public Aula cerrar(@PathVariable UUID id) {
        return service.cerrar(id);
    }
}
