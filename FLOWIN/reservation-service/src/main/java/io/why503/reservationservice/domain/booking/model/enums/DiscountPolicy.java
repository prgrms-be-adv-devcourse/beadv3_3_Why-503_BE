package io.why503.reservationservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 티켓에 적용 가능한 할인 정책 유형을 관리하는 열거형
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