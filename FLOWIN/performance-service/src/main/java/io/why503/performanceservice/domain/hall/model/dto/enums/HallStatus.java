package io.why503.performanceservice.domain.hall.model.dto.enums;

import io.why503.performanceservice.domain.hall.util.HallExceptionFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HallStatus {

    ACTIVE("ACTIVE"),       // 사용중
    INACTIVE("INACTIVE");   // 미사용

    private final String code;

    public static HallStatus fromCode(String code) {
        for (HallStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw HallExceptionFactory.hallBadRequest("유효하지 않은 Hall 상태 값: " + code);
    }
}
