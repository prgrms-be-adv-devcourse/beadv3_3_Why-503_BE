package io.why503.paymentservice.domain.payment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 결제 승인 요청 시 필요한 주문 정보와 포인트 사용 금액을 담는 객체
 */
public record PaymentRequest(
        @NotBlank(message = "주문 번호는 필수입니다.")
        String orderId,

        String paymentKey,

        @NotNull(message = "총 결제 금액은 필수입니다.")
        @Positive(message = "총 결제 금액은 양수여야 합니다.")
        Long amount,

        @PositiveOrZero(message = "포인트 사용 금액은 0원 이상이어야 합니다.")
        Long usePointAmount
) {
    // 포인트 사용 금액 미입력 시 0원으로 초기화
    public PaymentRequest {
        if (usePointAmount == null) {
            usePointAmount = 0L;
        }
    }
}