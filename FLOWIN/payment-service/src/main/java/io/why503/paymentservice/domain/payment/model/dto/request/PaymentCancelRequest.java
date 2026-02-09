package io.why503.paymentservice.domain.payment.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 결제 취소 처리에 필요한 정보를 담는 객체
 */
public record PaymentCancelRequest(
        @NotNull(message = "결제 식별자는 필수입니다.")
        Long paymentSq,

        @NotEmpty(message = "취소할 티켓 목록은 최소 1개 이상이어야 합니다.")
        List<Long> targetTicketSqs,

        String reason
) {
}