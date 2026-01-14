package io.why503.performanceservice.domain.concerthall.Model.Dto.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertHallStatus {

    ACTIVE("Y"),     // 사용중
    INACTIVE("N");   // 미사용

    private final String code;

    public static ConcertHallStatus fromCode(String code) {
        for (ConcertHallStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("invalid concert hall status: " + code);
    }
}
