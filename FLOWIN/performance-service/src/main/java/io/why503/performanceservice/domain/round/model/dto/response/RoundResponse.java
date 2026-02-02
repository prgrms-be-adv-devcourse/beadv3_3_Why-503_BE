package io.why503.performanceservice.domain.round.model.dto.response;


import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RoundResponse(
        @NotNull Long roundSq,
        @NotNull Long showSq,
        @NotNull LocalDateTime roundDateTime,
        @NotNull Integer roundNum,
        @NotBlank String roundCast,
        @NotBlank String roundStatusName,
        @NotNull RoundStatus roundStatus
) {
}