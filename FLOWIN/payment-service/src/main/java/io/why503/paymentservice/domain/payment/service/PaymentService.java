package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.payment.model.dto.request.PaymentRequest;
import io.why503.paymentservice.domain.payment.model.dto.response.PaymentResponse;

import java.util.List;

/**
 * 결제 도메인의 핵심 비즈니스 로직을 정의하는 서비스 인터페이스
 * - [땅땅땅 규칙] 예매 결제와 포인트 충전을 orderId 기반으로 분기하여 처리함
 * - [땅땅땅 규칙] 외부 PG사 및 타 서비스(Account, Reservation)와의 정합성을 보장함
 */
public interface PaymentService {

    /**
     * 통합 결제 승인 처리
     * - orderId 접두어에 따라 '예매 결제'와 '포인트 충전' 프로세스를 자동으로 분기함
     * @param userSq 결제 요청자 식별자
     * @param request 결제 승인에 필요한 파라미터 (orderId, paymentKey, 금액 등)
     * @return 결제 승인 결과 상세 정보
     */
    PaymentResponse pay(Long userSq, PaymentRequest request);

    /**
     * 결제 내역 상세 조회
     * @param userSq 조회 요청자 식별자 (본인 소유 확인용)
     * @param paymentSq 결제 고유 식별자
     * @return 결제 상세 정보
     */
    PaymentResponse findPayment(Long userSq, Long paymentSq);

    /**
     * 사용자의 전체 결제 이력 목록 조회
     * @param userSq 사용자 식별자
     * @return 최신순으로 정렬된 결제 이력 리스트
     */
    List<PaymentResponse> findPaymentsByUser(Long userSq);

    /**
     * 결제 취소 및 환불 프로세스 수행
     * - 결제 수단별(포인트/PG) 복구 및 예매 좌석 재고 해제를 연쇄적으로 처리함
     * @param userSq 취소 요청자 식별자
     * @param paymentSq 취소 대상 결제 식별자
     * @param reason 취소 사유
     * @return 취소 처리된 결제 결과 정보
     */
    PaymentResponse cancelPayment(Long userSq, Long paymentSq, String reason);
}