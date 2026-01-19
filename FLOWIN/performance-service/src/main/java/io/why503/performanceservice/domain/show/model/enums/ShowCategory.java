/**
 * Show Category Enum
 * 공연 카테고리 정의 Enum
 *
 * 사용 목적 :
 * - 공연 카테고리를 코드 값으로 관리
 * - DB 및 요청/응답에서 숫자 기반 처리 지원
 */
package io.why503.performanceservice.domain.show.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowCategory {

    CONCERT(0), // 콘서트
    MUSICAL(1), // 뮤지컬
    PLAY(2),    // 연극
    CLASSIC(3); // 클래식

    private final int code; // 카테고리 코드 값

    /**
     * 코드 값 기반 ShowCategory 변환
     *
     * 사용 위치 :
     * - ShowReqDto에서 전달받은 category 값 변환 시 사용
     *
     * @param code 카테고리 코드
     * @return ShowCategory Enum
     * @throws IllegalArgumentException 유효하지 않은 코드 입력 시
     */
    public static ShowCategory fromCode(int code) {
        for (ShowCategory value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("invalid show category code: " + code);
    }
}
