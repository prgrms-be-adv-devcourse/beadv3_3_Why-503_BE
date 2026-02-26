package io.why503.paymentservice.domain.payment.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 결제 취소 처리에 필요한 정보를 담는 객체
 */
public record PaymentCancelRequest(
        String reason,
        List<Long> seatIds
) {
}