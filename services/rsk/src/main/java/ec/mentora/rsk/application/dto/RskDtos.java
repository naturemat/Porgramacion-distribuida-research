package ec.mentora.rsk.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public final class RskDtos {

    public record RevisionRequest(
        @NotNull UUID revisorId,
        @NotBlank String decision,
        String observacion
    ) {
    }

    public record IntervencionRequest(
        @NotBlank String descripcion,
        @NotNull UUID responsableId
    ) {
    }

    private RskDtos() {
    }
}
