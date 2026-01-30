package io.why503.paymentservice.domain.booking.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 티켓 상태 VO
 * - 개별 좌석(티켓)의 생명주기를 관리합니다.
 */
@Getter
@AllArgsConstructor
public enum TicketStatus {
    AVAILABLE("예매가능"), // 초기 상태
    RESERVED("선점됨"),   // 결제 진행 중 (임시 점유)
    PAID("결제됨"),       // 결제 완료
    USED("사용됨"),       // 입장 완료 (QR 사용)
    CANCELLED("취소됨");  // 환불 또는 선점 취소

    private final String description;
}