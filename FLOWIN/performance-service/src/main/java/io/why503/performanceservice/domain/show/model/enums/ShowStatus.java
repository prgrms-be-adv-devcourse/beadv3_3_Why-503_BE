/**
 * Show Status Enum
 * 공연 진행 상태 정의 Enum
 *
 * 사용 목적 :
 * - 공연의 현재 상태를 코드 값으로 관리
 * - DB 및 비즈니스 로직에서 상태값 일관성 유지
 */
package io.why503.performanceservice.domain.show.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowStatus {

    SCHEDULED, // 공연 예정
    ONGOING,   // 공연 중
    ENDED,     // 공연 종료
    CANCELED;  // 공연 취소
}
