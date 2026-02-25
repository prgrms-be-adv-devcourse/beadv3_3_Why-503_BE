package io.why503.aiservice.domain.ai.model.vo;


public record Recommendations(
        // 장르타입 추가
        String showCategory,
        // 추천 이유
        String reason,
        //추천된 장르
        String showGenre
) {
}
