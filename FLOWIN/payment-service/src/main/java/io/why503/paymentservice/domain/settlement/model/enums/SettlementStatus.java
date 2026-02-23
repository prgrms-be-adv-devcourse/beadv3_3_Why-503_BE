package io.why503.paymentservice.domain.settlement.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {

    READY("정산 대기"),
    COMPLETED("정산 완료");

    private final String description;
}