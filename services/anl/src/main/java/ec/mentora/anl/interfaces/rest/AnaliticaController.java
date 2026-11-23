package ec.mentora.anl.interfaces.rest;

import ec.mentora.anl.application.AnaliticaService;
import ec.mentora.anl.domain.Tablero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AnaliticaController {

    private final AnaliticaService service;

    @GetMapping("/tableros")
    public List<Tablero> tableros() {
        return service.listarTableros();
    }

    @GetMapping("/tableros/{id}/datos")
    public Map<String, Object> datos(@PathVariable UUID id) {
        return service.datos(id);
    }

    @GetMapping("/metricas")
    public Map<String, Object> metricas() {
        return service.metricas();
    }
}
