package io.why503.performanceservice.domain.roundSeats.model.dto.request;

import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;
import jakarta.validation.constraints.NotNull;

public record RoundSeatStatusRequest(
        @NotNull(message = "변경할 상태 정보는 필수입니다.")
        RoundSeatStatus roundSeatStatus
) {
}
