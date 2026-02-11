package io.why503.reservationservice.domain.booking.model.dto.response;

import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;

public record BookingSeatResponse(
        Long roundSeatSq,
        DiscountPolicy discountPolicy // 중요: 적용된 할인 정보 반환
) {}