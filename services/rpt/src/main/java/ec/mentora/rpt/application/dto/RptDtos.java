package ec.mentora.rpt.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public final class RptDtos {

    public record SolicitarReporteRequest(
        @NotBlank String tipo,
        UUID aulaId,
        UUID periodoId
    ) {
    }

    private RptDtos() {
    }
}
