package ec.mentora.cnt.interfaces.rest;

import ec.mentora.cnt.application.ContenidoService;
import ec.mentora.cnt.application.dto.CntDtos.CrearContenidoRequest;
import ec.mentora.cnt.application.dto.CntDtos.CrearVersionRequest;
import ec.mentora.cnt.application.dto.CntDtos.EnlaceArchivoRequest;
import ec.mentora.cnt.application.dto.CntDtos.EnlaceResponse;
import ec.mentora.cnt.domain.Contenido;
import ec.mentora.cnt.domain.VersionContenido;
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
public class ContenidoController {

    private final ContenidoService service;

    @PostMapping("/contenidos")
    @ResponseStatus(HttpStatus.CREATED)
    public Contenido crear(@Valid @RequestBody CrearContenidoRequest request) {
        return service.crear(request);
    }

    @GetMapping("/contenidos")
    public List<Contenido> listar(@RequestParam UUID aulaId) {
        return service.listar(aulaId);
    }

    @PostMapping("/contenidos/{id}/versiones")
    @ResponseStatus(HttpStatus.CREATED)
    public VersionContenido crearVersion(@PathVariable UUID id, @Valid @RequestBody CrearVersionRequest request) {
        return service.crearVersion(id, request);
    }

    @PostMapping("/contenidos/{id}/publicar")
    public Contenido publicar(@PathVariable UUID id) {
        return service.publicar(id);
    }

    @PostMapping("/archivos/enlace")
    @ResponseStatus(HttpStatus.CREATED)
    public EnlaceResponse enlace(@Valid @RequestBody EnlaceArchivoRequest request) {
        return service.enlace(request);
    }
}
