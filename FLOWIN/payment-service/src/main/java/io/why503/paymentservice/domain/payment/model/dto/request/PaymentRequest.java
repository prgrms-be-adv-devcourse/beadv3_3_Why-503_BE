package io.why503.paymentservice.domain.payment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentRequest(
        @NotBlank(message = "주문 번호는 필수입니다.")
        String orderId,

        // PG사 결제 키 (전액 포인트 결제 시 null 가능)
        String paymentKey,

        @NotNull(message = "총 결제 금액은 필수입니다.")
        @Positive(message = "총 결제 금액은 양수여야 합니다.")
        Long amount,

        @PositiveOrZero(message = "포인트 사용 금액은 0원 이상이어야 합니다.")
        Long usePointAmount
) {
    // 생성자에서 null 처리 (Optional)
    // record는 불변이므로 Compact Constructor를 통해 기본값 설정 가능
    public PaymentRequest {
        if (usePointAmount == null) {
            usePointAmount = 0L;
        }
    }
}