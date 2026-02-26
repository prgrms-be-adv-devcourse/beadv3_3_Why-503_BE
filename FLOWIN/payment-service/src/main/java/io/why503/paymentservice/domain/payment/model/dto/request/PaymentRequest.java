package io.why503.paymentservice.domain.payment.model.dto.request;

import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 결제 승인 요청에 필요한 정보를 전달하는 객체
 * - 외부 결제 시스템 승인에 필요한 키 값과 금액 정보를 포함
 */
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