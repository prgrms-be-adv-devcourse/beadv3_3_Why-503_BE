package io.why503.paymentservice.domain.booking.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 할인 정책 VO
 * - 티켓에 적용된 할인 종류를 관리합니다.
 * - 사용자가 요청한 청소년, 노약자, 장애인, 국가유공자 할인을 포함합니다.
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

    // String -> Enum 변환 메서드
    public static DiscountPolicy from(String policy) {
        // 해피 패스 금지: null 또는 빈 값 검증
        if (policy == null || policy.isBlank()) {
            throw new IllegalArgumentException("DiscountPolicy는 필수 값입니다.");
        }

        // 메서드 참조(::) 금지: 람다식 사용
        return Arrays.stream(DiscountPolicy.values())
                .filter(p -> p.name().equalsIgnoreCase(policy))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 할인 정책입니다: " + policy));
    }
}