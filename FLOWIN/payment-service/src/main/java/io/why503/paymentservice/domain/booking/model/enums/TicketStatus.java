package io.why503.paymentservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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

    // String -> Enum 변환 메서드
    public static TicketStatus from(String status) {
        // 해피 패스 금지: null 또는 빈 값 검증
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("TicketStatus는 필수 값입니다.");
        }

        // 메서드 참조(::) 금지: 람다식 사용
        return Arrays.stream(TicketStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 TicketStatus 입니다: " + status));
    }
}