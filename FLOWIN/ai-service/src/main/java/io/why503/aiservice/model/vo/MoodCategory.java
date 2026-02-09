package io.why503.aiservice.model.vo;

import java.util.Optional;

public enum MoodCategory {
    //판타지
    FANTASY,
    //영웅
    HORROR,
    //로맨스
    ROMANCE,
    //코미디
    COMEDY,
    //액션
    ACTION;

    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static Optional<MoodCategory> fromString(String value) {
        try {
            return Optional.of(valueOf(value.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
