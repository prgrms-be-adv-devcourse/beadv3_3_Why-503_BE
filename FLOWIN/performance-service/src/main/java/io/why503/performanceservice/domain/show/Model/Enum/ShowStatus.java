/**
 * Show Status Enum
 * 공연 진행 상태 정의 Enum
 *
 * 사용 목적 :
 * - 공연의 현재 상태를 코드 값으로 관리
 * - DB 및 비즈니스 로직에서 상태값 일관성 유지
 */
package io.why503.performanceservice.domain.show.Model.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowStatus {

    SCHEDULED(0), // 공연 예정
    ONGOING(1),   // 공연 중
    ENDED(2),     // 공연 종료
    CANCELED(3);  // 공연 취소

    private final int code; // 공연 상태 코드 값

    /**
     * 코드 값 기반 ShowStatus 변환
     *
     * 사용 위치 :
     * - 공연 생성 시 기본 상태 설정
     * - 상태 코드 기반 Enum 변환 처리
     *
     * @param code 공연 상태 코드
     * @return ShowStatus Enum
     * @throws IllegalArgumentException 유효하지 않은 코드 입력 시
     */
    public static ShowStatus fromCode(int code) {
        for (ShowStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("invalid show status code: " + code);
    }
}
