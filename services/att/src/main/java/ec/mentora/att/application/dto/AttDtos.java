package ec.mentora.att.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public final class AttDtos {

    public record AbrirSesionRequest(
        @NotNull UUID aulaId,
        @NotNull UUID periodoId,
        @NotNull LocalDate fecha,
        UUID abiertaPor
    ) {
    }

    public record RegistroRequest(
        @NotNull UUID estudianteId,
        @NotBlank String estado,
        String observacion
    ) {
    }

    public record JustificacionRequest(
        @NotNull UUID sesionId,
        @NotNull UUID estudianteId,
        @NotBlank String motivo,
        UUID evidenciaArchivoId
    ) {
    }

    private AttDtos() {
    }
}
