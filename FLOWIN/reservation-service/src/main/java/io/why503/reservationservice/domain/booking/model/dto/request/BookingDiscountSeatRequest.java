package io.why503.reservationservice.domain.booking.model.dto.request;

import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;
import jakarta.validation.constraints.NotNull;

public record BookingDiscountSeatRequest(
        @NotNull(message = "좌석 ID는 필수입니다.")
        Long roundSeatSq,

        DiscountPolicy discountPolicy // 없으면 null (서비스에서 NONE 처리)
) {}