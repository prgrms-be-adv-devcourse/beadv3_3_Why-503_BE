package io.why503.aiservice.model.vo;

import java.util.Optional;

public enum Category {
    //장르 속성 추가
    CONCERT("현장감, 몰입"), MUSICAL("감동적, 스토리 중심"), PLAY("대화 중심"), CLASSIC("차분, 정제된 분위기");


    private final String mood; // 분위기/테마

    Category(String mood) {
        this.mood = mood;
    }

    public String getMood() {
        return mood;
    }

    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static Optional<Category> fromString(String value) {
        try {
            return Optional.of(valueOf(value.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
