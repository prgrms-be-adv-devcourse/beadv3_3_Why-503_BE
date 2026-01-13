package io.why503.performanceservice.domain.show.Model.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShowStatus {

    SCHEDULED(0), // 공연예정
    ONGOING(1), // 공연중
    ENDED(2), // 공연종료
    CANCELED(3); // 공연취소

    private final int code;

    public static ShowStatus fromCode(int code) {
        for (ShowStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("invalid show status code: " + code);
    }
}
