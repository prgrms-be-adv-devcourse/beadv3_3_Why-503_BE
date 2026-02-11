package io.why503.reservationservice.domain.booking.model.dto.request;

import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;
import jakarta.validation.constraints.NotNull;

/**
 * 개별 좌석별로 적용하고자 하는 할인 혜택 정보를 전달하는 객체
 */
public record BookingDiscountSeatRequest(
        @NotNull(message = "좌석 ID는 필수입니다.")
        Long roundSeatSq,

        DiscountPolicy discountPolicy
) {}