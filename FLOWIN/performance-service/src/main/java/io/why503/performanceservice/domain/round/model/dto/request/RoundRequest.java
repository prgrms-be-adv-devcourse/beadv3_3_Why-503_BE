package io.why503.performanceservice.domain.round.model.dto.request;


import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RoundRequest(
        @NotBlank Long showSq,
        @NotNull LocalDateTime roundDt,
        @NotBlank String roundCast,
        @NotNull RoundStatus roundStatus
) {
}