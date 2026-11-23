package ec.mentora.asg.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class AsgDtos {

    public record CrearTareaRequest(
        @NotNull UUID aulaId,
        UUID unidadId,
        @NotNull UUID periodoId,
        @NotBlank String titulo,
        String descripcion,
        @NotNull Instant fechaLimite,
        @NotNull BigDecimal puntajeMaximo
    ) {
    }

    public record EntregaRequest(
        @NotNull UUID estudianteId,
        String contenido,
        UUID archivoId
    ) {
    }

    public record CalificarRequest(@NotNull BigDecimal puntaje, String retroalimentacion) {
    }

    private AsgDtos() {
    }
}
