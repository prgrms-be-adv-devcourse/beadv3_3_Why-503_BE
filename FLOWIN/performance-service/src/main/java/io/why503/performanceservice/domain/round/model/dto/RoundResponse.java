package io.why503.performanceservice.domain.round.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RoundResponse(
        @NotNull Long roundSq,
        @NotNull Long showSq,
        @NotNull LocalDateTime roundDt,
        @NotNull Integer roundNum,
        @NotBlank String roundCast,
        @NotBlank String roundStatusName,
        @NotNull RoundStatus roundStatus
) {
}