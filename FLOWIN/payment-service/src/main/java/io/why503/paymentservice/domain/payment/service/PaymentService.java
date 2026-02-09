package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;

import java.util.List;

/**
 * 결제 도메인의 핵심 비즈니스 로직을 정의하는 서비스 인터페이스
 * - 예매 및 포인트 충전 프로세스 제어와 외부 시스템 간의 데이터 정합성 보장
 */
public interface PaymentService {

    // 주문 유형별 승인 프로세스 분기 및 최종 결제 확정 처리
    PaymentResponse pay(Long userSq, PaymentRequest request);

    PaymentResponse findPayment(Long userSq, Long paymentSq);

    List<PaymentResponse> findPaymentsByUser(Long userSq);

    // 결제 수단별 자산 복구 및 관련 서비스의 점유 정보 해제 수행
    PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason);
}