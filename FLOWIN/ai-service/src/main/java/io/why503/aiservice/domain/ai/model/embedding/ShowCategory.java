package io.why503.aiservice.domain.ai.model.embedding;

import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.global.exception.AiException;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

//장르 속성 추가
public enum ShowCategory {
    //콘서트
    CONCERT(Set.of(ConcertType.values())),
    //뮤지컬
    MUSICAL(Set.of(MusicalType.values())),
    //연극
    PLAY(Set.of(PlayType.values())),
    //클래식
    CLASSIC(Set.of(ClassicType.values()));
    //Category.CONCERT.supports(ConcertType.ROCK);

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
    ShowCategory(Set<? extends ShowGenre> types) {
        this.types = types;
    }


    //속성 json 인식할 때 문자열만 인식으로 인한 장르 문자열 바꿔줌
    public static ShowCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            throw AiException.invalidCategory();
        }

        String raw = value.trim();

        //alias 먼저 체크
        if (ALIAS.containsKey(raw)) {
            return ALIAS.get(raw);
        }

        //enum 직접 매칭
        try {
            return ShowCategory.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        //특수문자 제거 후 매칭
        String filtered = raw.replaceAll("[^a-zA-Z가-힣]", "");
        try {
            return ShowCategory.valueOf(filtered.toUpperCase());
        } catch (IllegalArgumentException ignored) {}

        throw AiException.invalidCategory();
    }


    //공연 종류 찾기
    public ShowGenre findShowType(String category) {

        if (category == null || category.isBlank()) {
            throw AiException.invalidCategory();
        }

        String normalized = category.trim().toLowerCase();

        return types.stream()
                .filter(type -> type.matches(normalized))
                .findFirst()
                .orElseThrow(() ->
                        AiException.NotFound(category)
                );
    }

    public boolean supports(ShowGenre genre) {
        return types.contains(genre);
    }
}
