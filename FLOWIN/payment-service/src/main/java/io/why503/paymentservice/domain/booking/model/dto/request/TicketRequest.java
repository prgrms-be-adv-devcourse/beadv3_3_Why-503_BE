package io.why503.paymentservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TicketRequest(
        @NotNull(message = "좌석 ID는 필수입니다.")
        @Positive(message = "좌석 ID는 양수여야 합니다.")
        Long roundSeatSq,

        // 할인 정책 (Null 허용 -> Mapper에서 NONE 처리)
        // String으로 입력받아 Enum.from()으로 안전하게 변환
        String discountPolicy
) {
}