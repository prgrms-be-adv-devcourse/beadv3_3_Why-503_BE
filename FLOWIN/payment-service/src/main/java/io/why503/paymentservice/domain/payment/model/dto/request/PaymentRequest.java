package io.why503.paymentservice.domain.payment.model.dto.request;

import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotBlank(message = "주문 ID는 필수입니다.")
        String orderId,

        @NotBlank(message = "PG 결제 키는 필수입니다.")
        String paymentKey,

        @NotNull(message = "결제 수단은 필수입니다.")
        PaymentMethod method,

        @NotNull(message = "총 결제 금액은 필수입니다.")
        @Min(0)
        Long totalAmount,

        @NotNull(message = "포인트 사용액은 필수입니다.")
        @Min(0)
        Long usePointAmount
) { }