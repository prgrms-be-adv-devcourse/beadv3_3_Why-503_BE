package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;

import java.util.stream.Collectors;

//공연의 종류마다 이름을 찾거나 category 책임 분리하여 복잡한 코드 -> 단순화 코드 (공연의 종류 찾을 때)
public interface ShowGenre {

    //공연 카테고리
    ShowCategory getCategory();
    //공연 장르
    String getName();

    //카테고리와 장르 연결
    default boolean matches(String value) {
        return getName().equalsIgnoreCase(value);
    }
}
