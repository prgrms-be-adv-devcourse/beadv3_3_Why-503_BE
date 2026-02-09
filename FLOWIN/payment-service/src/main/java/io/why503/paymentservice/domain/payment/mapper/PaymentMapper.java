package io.why503.paymentservice.domain.payment.mapper;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;
import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import org.springframework.stereotype.Component;

/**
 * 결제 관련 엔티티와 데이터 전송 객체 간의 변환을 담당하는 컴포넌트
 * - 도메인 모델 보호 및 클라이언트 응답 형식 규격화
 */
@Component
public class PaymentMapper {

    // 결제 이력 정보를 외부 응답 형식으로 가공
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

    // 예매 서비스의 결제 요청 데이터를 도메인 엔티티로 변환
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

    // 자체 포인트 충전 요청에 따른 결제 엔티티 생성
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