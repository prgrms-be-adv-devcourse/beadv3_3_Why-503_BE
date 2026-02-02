package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;

import java.util.List;

/**
 * 결제 승인, 조회 및 취소 등의 핵심 비즈니스 로직을 정의하는 서비스 인터페이스
 */
public interface PaymentService {

    // 예매 또는 포인트 충전 건에 대한 결제 승인 처리
    PaymentResponse pay(Long userSq, PaymentRequest request);

    // 특정 결제 건에 대한 상세 내역 조회
    PaymentResponse findPayment(Long userSq, Long paymentSq);

    // 사용자의 전체 결제 이력 목록 조회
    List<PaymentResponse> findPaymentsByUser(Long userSq);

    // 승인된 결제 건의 취소 및 환불 처리
    PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason);
}