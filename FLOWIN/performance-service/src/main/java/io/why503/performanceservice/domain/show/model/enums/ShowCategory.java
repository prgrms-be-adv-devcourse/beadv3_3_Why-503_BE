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

    CONCERT, // 콘서트
    MUSICAL, // 뮤지컬
    PLAY,  // 연극
    CLASSIC; // 클래식
}
