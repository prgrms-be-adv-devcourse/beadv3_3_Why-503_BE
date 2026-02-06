package io.why503.aiservice.model.vo;

import lombok.Getter;
import java.util.*;

//장르 속성 추가
public enum Category {
    //콘서트
    CONCERT("현장감, 몰입", Set.of(ConcertType.values())),
    //뮤지컬
    MUSICAL("감동, 서사, 스토리 중심", Set.of(MusicalType.values())),
    //연극
    PLAY("대화 중심", Set.of(PlayType.values())),
    //클래식
    CLASSIC("차분, 정제된 분위기", Set.of(ClassicType.values()));
    //Category.CONCERT.supports(ConcertType.ROCK);

    @Getter
    private final String mood;
    @Getter
    private final Set<? extends ShowCategory> types;


    //카테고리 생성자
    Category(String mood, Set<? extends ShowCategory> types) {
        this.mood = mood;
        this.types = types;
    }


    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static Optional<Category> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        // 1. 원본 그대로
        Optional<Category> direct = tryParse(value);
        if (direct.isPresent()) return direct;

        // 2. 공백 제거 + 대소문자
        String normalized = value.trim().toUpperCase();
        Optional<Category> normalizedResult = tryParse(normalized);
        if (normalizedResult.isPresent()) return normalizedResult;

        // 3. 특수문자 제거
        String filtered = normalized.replaceAll("[^A-Z]", "");
        Optional<Category> filteredResult = tryParse(filtered);
        if (filteredResult.isPresent()) return filteredResult;

        // 4. 실패시 null
        return Optional.empty();
    }

    private static Optional<Category> tryParse(String value) {
        try {
            return Optional.of(Category.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }



    public ShowCategory findShowType(String raw) {

        if (raw == null || raw.isBlank()) {
            return types.stream().findFirst().orElse(null);
        }

        String normalized = raw.trim().toLowerCase();

        return types.stream()
                .filter(type ->
                        type.typeName().equalsIgnoreCase(normalized)
                                || ((Enum<?>) type).name().equalsIgnoreCase(normalized)
                )
                .findFirst()
                .orElse(null);
    }
}
