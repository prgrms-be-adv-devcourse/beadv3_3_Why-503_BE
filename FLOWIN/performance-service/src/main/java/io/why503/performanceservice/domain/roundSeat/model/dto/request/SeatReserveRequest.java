package io.why503.performanceservice.domain.roundSeat.model.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SeatReserveRequest(
        @NotEmpty(message = "좌석 번호 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {}
