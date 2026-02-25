package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;


//공연의 종류마다 이름을 찾거나 category 책임 분리하여 복잡한 코드 -> 단순화 코드 (공연의 종류 찾을 때)
public interface ShowGenre {

    ShowCategory getCategory();
    String getName();

    default boolean matches(String value) {
        if (value == null || value.isBlank()) return false;

        String raw = value.trim();

        // enum 이름 비교 (OPERA 등)
        if (this.toString().equalsIgnoreCase(raw)) {
            return true;
        }

        // 한글 이름 비교 (오페라 등)
        if (getName() != null && getName().equalsIgnoreCase(raw)) {
            return true;
        }

        return false;
    }
}