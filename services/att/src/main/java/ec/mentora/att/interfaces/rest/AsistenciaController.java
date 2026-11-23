package ec.mentora.att.interfaces.rest;

import ec.mentora.att.application.AsistenciaService;
import ec.mentora.att.application.dto.AttDtos.AbrirSesionRequest;
import ec.mentora.att.application.dto.AttDtos.JustificacionRequest;
import ec.mentora.att.application.dto.AttDtos.RegistroRequest;
import ec.mentora.att.domain.Justificacion;
import ec.mentora.att.domain.RegistroAsistencia;
import ec.mentora.att.domain.SesionAsistencia;
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

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    @PostMapping("/sesiones")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionAsistencia abrir(@Valid @RequestBody AbrirSesionRequest request) {
        return service.abrir(request);
    }

    @PostMapping("/sesiones/{id}/registros")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroAsistencia registrar(@PathVariable UUID id, @Valid @RequestBody RegistroRequest request) {
        return service.registrar(id, request);
    }

    @PostMapping("/sesiones/{id}/cerrar")
    public SesionAsistencia cerrar(@PathVariable UUID id) {
        return service.cerrar(id);
    }

    @PostMapping("/justificaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Justificacion solicitar(@Valid @RequestBody JustificacionRequest request) {
        return service.solicitar(request);
    }

    @PostMapping("/justificaciones/{id}/aprobar")
    public Justificacion aprobar(@PathVariable UUID id) {
        return service.aprobar(id);
    }

    @PostMapping("/justificaciones/{id}/rechazar")
    public Justificacion rechazar(@PathVariable UUID id) {
        return service.rechazar(id);
    }

    @GetMapping("/sesiones/{id}/registros")
    public java.util.List<RegistroAsistencia> listar(@PathVariable UUID id) {
        return service.registros(id);
    }
}
