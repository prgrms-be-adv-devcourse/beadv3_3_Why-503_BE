/**
 * Show Status Enum
 * 공연 진행 상태 정의 Enum
 */
package io.why503.performanceservice.domain.show.model.enums;

import io.why503.performanceservice.domain.show.util.ShowExceptionFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowStatus {

    SCHEDULED, // 공연 예정
    ONGOING,   // 공연 중
    ENDED,     // 공연 종료
    CANCELED;  // 공연 취소
    
    public static ShowStatus fromCode(String code) {
        try {
            return ShowStatus.valueOf(code);
        } catch (Exception e) {
            throw ShowExceptionFactory.showBadRequest(
                "유효하지 않은 ShowStatus 값: " + code
            );
        }
    }
}
