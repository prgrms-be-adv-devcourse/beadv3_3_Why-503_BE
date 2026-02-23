package io.why503.aiservice.domain.ai.model.embedding;

import lombok.Getter;
import java.util.*;

//장르 속성 추가
public enum ShowCategory {
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
    private final Set<? extends ShowGenre> types;

    //카테고리 필드값 인식
    private static final Map<String, ShowCategory> ALIAS = Map.of(
            "뮤지컬", MUSICAL,
            "musical", MUSICAL,
            "대중음악", CONCERT,
            "concert", CONCERT,
            "연극", PLAY,
            "play", PLAY,
            "서양음악(클래식)", CLASSIC,
            "classic", CLASSIC
    );


    //카테고리 생성자
    ShowCategory(String mood, Set<? extends ShowGenre> types) {
        this.mood = mood;
        this.types = types;
    }


    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static Optional<ShowCategory> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String raw = value.trim();

        //alias 먼저 체크
        if (ALIAS.containsKey(raw)) {
            return Optional.of(ALIAS.get(raw));
        }

        //enum 직접 매칭
        Optional<ShowCategory> direct = Parse(raw.toUpperCase());
        if (direct.isPresent()) return direct;

        //특수문자 제거 후 매칭
        String filtered = raw.replaceAll("[^a-zA-Z가-힣]", "");
        Optional<ShowCategory> filteredResult = Parse(filtered.toUpperCase());
        if (filteredResult.isPresent()) return filteredResult;

        return Optional.empty();
    }

    private static Optional<ShowCategory> Parse(String value) {
        try {
            return Optional.of(ShowCategory.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    //공연 종류 찾기
    public ShowGenre findShowType(String category) {

        if (category == null || category.isBlank()) {
            return types.stream().findFirst().orElse(null);
        }

        String normalized = category.trim().toLowerCase();

        return types.stream()
                .filter(type -> type.matches(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 장르 없음"));
    }
}
