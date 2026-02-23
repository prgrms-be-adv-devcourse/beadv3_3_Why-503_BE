package io.why503.aiservice.domain.ai.model.vo;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.ShowGenre;

public record Recommendations(
        // 장르타입 추가
        ShowCategory showCategory,
        // 추천 이유
        String reason,
        //추천된 장르
        ShowGenre showGenre
) {
}
