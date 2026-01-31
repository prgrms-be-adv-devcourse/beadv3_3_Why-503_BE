package io.why503.paymentservice.domain.payment.mapper;

import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /**
     * Payment Entity -> PaymentResponse DTO 변환
     */
    public PaymentResponse entityToResponse(Payment payment) {
        // 해피 패스 금지: Entity 필수 검증
        if (payment == null) {
            throw new IllegalArgumentException("변환할 Payment Entity는 필수입니다.");
        }

        return new PaymentResponse(
                payment.getSq(),
                payment.getOrderId(),

                // 참조 타입 (BOOKING, POINT)
                payment.getRefType().name(),

                // 결제 수단 (Code + Description)
                payment.getMethod().name(),
                payment.getMethod().getDescription(),

                // 결제 상태 (Code + Description)
                payment.getStatus().name(),
                payment.getStatus().getDescription(),

                // 금액 상세
                payment.getTotalAmount(),
                payment.getPgAmount(),
                payment.getPointAmount(),

                // 일시 정보
                payment.getApprovedDt(),
                payment.getCancelledDt(),
                payment.getCreatedDt()
        );
    }
}