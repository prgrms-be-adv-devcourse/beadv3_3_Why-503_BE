package io.why503.paymentservice.domain.booking.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예매 상태 VO
 * - 예매의 전체적인 진행 상황을 관리합니다.
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {
    PENDING("예매대기"),       // 결제 전
    CONFIRMED("예매완료"),      // 결제 완료
    CANCELLED("예매취소"),      // 전체 취소
    PARTIAL_CANCEL("부분취소"); // 일부 티켓만 취소됨

    private final String description;
}