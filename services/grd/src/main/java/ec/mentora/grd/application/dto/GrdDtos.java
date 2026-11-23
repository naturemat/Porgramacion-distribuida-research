package ec.mentora.grd.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class GrdDtos {

    public record CrearComponenteRequest(
        @NotNull UUID aulaId,
        @NotNull UUID periodoId,
        @NotBlank String nombre,
        @NotNull BigDecimal peso
    ) {
    }

    public record RegistrarNotaRequest(
        @NotNull UUID aulaId,
        @NotNull UUID periodoId,
        @NotNull UUID estudianteId,
        @NotNull UUID componenteId,
        @NotNull BigDecimal valor,
        @NotBlank String origen,
        UUID origenId
    ) {
    }

    public record NotaLoteRequest(@NotNull List<RegistrarNotaRequest> notas) {
    }

    public record CorreccionRequest(
        @NotNull BigDecimal valor,
        @NotBlank String motivo,
        UUID creadaPor
    ) {
    }

    public record CierreRequest(@NotNull UUID aulaId, @NotNull UUID periodoId) {
    }

    private GrdDtos() {
    }
}
