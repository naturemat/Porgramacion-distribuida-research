package ec.mentora.cue.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class CueDtos {

    public record OpcionRequest(@NotBlank String texto, boolean correcta) {
    }

    public record PreguntaRequest(
        @NotBlank String enunciado,
        @NotBlank String tipo,
        @NotNull BigDecimal puntaje,
        List<OpcionRequest> opciones
    ) {
    }

    public record CrearCuestionarioRequest(
        @NotNull UUID aulaId,
        UUID unidadId,
        @NotNull UUID periodoId,
        @NotBlank String titulo,
        int intentosMaximos,
        List<PreguntaRequest> preguntas
    ) {
    }

    public record IniciarIntentoRequest(@NotNull UUID estudianteId) {
    }

    public record ResponderRequest(
        @NotNull UUID preguntaId,
        UUID opcionId,
        String texto
    ) {
    }

    private CueDtos() {
    }
}
