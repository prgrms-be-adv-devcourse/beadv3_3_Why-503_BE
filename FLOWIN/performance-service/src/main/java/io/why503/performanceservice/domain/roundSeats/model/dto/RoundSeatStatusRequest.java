package io.why503.performanceservice.domain.roundSeats.model.dto;

import jakarta.validation.constraints.NotNull;

public record RoundSeatStatusRequest(
        @NotNull(message = "변경할 상태 정보는 필수입니다.")
        RoundSeatStatus roundSeatStatus
) {
}
