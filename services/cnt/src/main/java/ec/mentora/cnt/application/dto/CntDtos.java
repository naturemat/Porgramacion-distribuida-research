package ec.mentora.cnt.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public final class CntDtos {

    public record CrearContenidoRequest(
        @NotNull UUID aulaId,
        UUID unidadId,
        @NotBlank String titulo
    ) {
    }

    public record CrearVersionRequest(@NotBlank String cuerpo, UUID archivoId) {
    }

    public record EnlaceArchivoRequest(
        @NotNull UUID contenidoId,
        @NotNull UUID archivoId,
        @NotBlank String nombre,
        @NotBlank String tipoMime,
        long tamano
    ) {
    }

    public record EnlaceResponse(UUID archivoId, String uploadUrl) {
    }

    private CntDtos() {
    }
}
