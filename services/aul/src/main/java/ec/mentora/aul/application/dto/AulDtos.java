package ec.mentora.aul.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public final class AulDtos {

    public record CrearAulaRequest(
        @NotBlank String nombre,
        @NotNull UUID periodoId,
        Instant fechaInicio,
        Instant fechaFin
    ) {
    }

    public record CrearMatriculaRequest(@NotNull UUID estudianteId) {
    }

    public record CrearUnidadRequest(@NotBlank String titulo, int orden) {
    }

    public record CrearAnuncioRequest(@NotBlank String titulo, @NotBlank String contenido) {
    }

    public record ActividadRequest(@NotNull Instant hora, @NotBlank String contadores) {
    }

    private AulDtos() {
    }
}
