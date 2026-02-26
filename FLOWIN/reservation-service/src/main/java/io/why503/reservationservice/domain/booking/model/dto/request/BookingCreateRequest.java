package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingCreateRequest(

        @NotNull(message = "회차 ID(roundSq)는 필수입니다.")
        Long roundSq,

        @NotEmpty(message = "예매할 좌석 ID 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {
}