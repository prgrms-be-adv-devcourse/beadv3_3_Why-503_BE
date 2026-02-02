package io.why503.paymentservice.domain.payment.mapper;

import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import org.springframework.stereotype.Component;

/**
 * 결제 엔티티 데이터를 응답용 객체로 변환하는 컴포넌트
 */
@Component
public class PaymentMapper {

    // 결제 엔티티를 상태 및 수단 설명이 포함된 응답 객체로 변환
    public PaymentResponse entityToResponse(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("변환할 Payment Entity는 필수입니다.");
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
                payment.getCanceledDt(),
                payment.getCreatedDt()
        );
    }

    public Payment responseToBookingEntity(Long userSq, String orderId, PaymentMethod method, Long totalAmount, Long pgAmount, Long pointAmount) {
        return Payment.builder()
                .userSq(userSq)
                .orderId(orderId)
                .refType(PaymentRefType.BOOKING)
                .method(method)
                .totalAmount(totalAmount)
                .pgAmount(pgAmount)
                .pointAmount(pointAmount)
                .build();
    }

    public Payment responseToPointEntity(Long userSq, String orderId, Long amount) {
        return Payment.builder()
                .userSq(userSq)
                .orderId(orderId)
                .refType(PaymentRefType.POINT)
                .method(PaymentMethod.CARD)
                .totalAmount(amount)
                .pgAmount(amount)
                .pointAmount(0L)
                .build();
    }
}