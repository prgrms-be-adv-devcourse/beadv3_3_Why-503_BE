package io.why503.paymentservice.domain.payment.mapper;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /**
     * 엔티티 -> 응답 DTO 변환 (12개 인수 전달)
     */
    public PaymentResponse entityToResponse(Payment payment) {
        if (payment == null) {
            throw PaymentExceptionFactory.paymentBadRequest("변환할 결제 정보가 없습니다.");
        }

        return new PaymentResponse(
                payment.getSq(),
                payment.getOrderId(),
                payment.getRefType().name(),
                payment.getMethod().name(),
                payment.getMethod().getDescription(),
                payment.getStatus().name(),
                payment.getStatus().getDescription(),
                payment.getTotalAmount(),
                payment.getPgAmount(),
                payment.getPointAmount(),
                payment.getApprovedDt(),
                payment.getCreatedDt()
        );
    }

    /**
     * [예매용] 요청 DTO -> 엔티티 변환 (계산 로직 포함)
     */
    public Payment responseToBookingEntity(Long userSq, Long bookingSq, PaymentRequest request) {
        if (request == null) {
            throw PaymentExceptionFactory.paymentBadRequest("결제 요청 정보가 누락되었습니다.");
        }

        long pgAmount = request.totalAmount() - request.usePointAmount();

        return Payment.builder()
                .userSq(userSq)
                .refType(PaymentRefType.BOOKING)
                .bookingSq(bookingSq)
                .orderId(request.orderId())
                .method(request.method())
                .totalAmount(request.totalAmount())
                .pgAmount(pgAmount)
                .pointAmount(request.usePointAmount())
                .build();
    }

    /**
     * [포인트 충전용] 요청 데이터 -> 엔티티 변환
     */
    public Payment responseToPointEntity(Long userSq, String orderId, Long amount) {
        return Payment.builder()
                .userSq(userSq)
                .refType(PaymentRefType.POINT)
                .orderId(orderId)
                .method(PaymentMethod.CARD)
                .totalAmount(amount)
                .pgAmount(amount)
                .pointAmount(0L)
                .build();
    }
}