package io.why503.performanceservice.domain.show.Model.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowCategory {

    CONCERT(0), // 콘서트
    MUSICAL(1), // 뮤지컬
    PLAY(2), // 연극
    CLASSIC(3); // 클래식

    private final int code;

    public static ShowCategory fromCode(int code) {
        for (ShowCategory value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("invalid show category code: " + code);
    }
}
