package io.why503.reservationservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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