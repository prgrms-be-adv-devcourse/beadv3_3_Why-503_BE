package io.why503.reservationservice.domain.booking.model.dto.response;

import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;

/**
 * 예매에 포함된 개별 좌석의 식별 정보와 적용된 혜택 유형
 */
public record BookingSeatResponse(
        Long roundSeatSq,
        DiscountPolicy discountPolicy
) {}