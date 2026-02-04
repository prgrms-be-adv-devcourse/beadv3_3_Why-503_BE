package io.why503.paymentservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 예매할 개별 좌석의 식별자와 적용할 할인 정책을 담는 요청 객체
 */
public record TicketRequest(
        @NotNull(message = "회차좌석")
        @Positive(message = "좌석 번호는 양수여야 합니다.")
        Long roundSeatSq,

        @NotBlank(message = "할인내용")
        String discountPolicy
) {
}