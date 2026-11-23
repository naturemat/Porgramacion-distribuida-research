package ec.mentora.atc.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class AtcDtos {

    public record CrearUmbralRequest(
        @NotBlank String nombre,
        @NotNull BigDecimal porcentaje
    ) {
    }

    private AtcDtos() {
    }
}
