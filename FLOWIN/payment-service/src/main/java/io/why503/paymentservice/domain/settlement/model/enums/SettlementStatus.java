package io.why503.paymentservice.domain.settlement.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 정산 프로세스의 진행 상태를 정의하고 관리하는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum SettlementStatus {

    READY("정산 대기"),
    COMPLETED("정산 완료");

    private final String description;
}