package io.why503.paymentservice.domain.ticket.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 개별 티켓의 생명주기 상태를 관리하는 열거형
 */
@Getter
@AllArgsConstructor
public enum TicketStatus {
    AVAILABLE("예매가능"),
    RESERVED("선점됨"),
    PAID("결제됨"),
    USED("사용됨"),
    CANCELLED("취소됨");

    private final String description;

    // 입력받은 문자열에 해당하는 티켓 상태 반환
    public static TicketStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("TicketStatus는 필수 값입니다.");
        }

        return Arrays.stream(TicketStatus.values())
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 TicketStatus 입니다: " + status));
    }
}