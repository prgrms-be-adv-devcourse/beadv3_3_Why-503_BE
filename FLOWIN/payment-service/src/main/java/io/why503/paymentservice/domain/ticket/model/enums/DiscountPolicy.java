package io.why503.paymentservice.domain.ticket.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 티켓 금액 계산 시 적용되는 할인 대상 및 유형에 대한 구분
 */
@Getter
@AllArgsConstructor
public enum DiscountPolicy {
    NONE("할인 없음"),
    YOUTH("청소년 할인"),
    SENIOR("노약자 할인"),
    DISABLED("장애인 할인"),
    VETERAN("국가유공자 할인");

    private final String description;
}