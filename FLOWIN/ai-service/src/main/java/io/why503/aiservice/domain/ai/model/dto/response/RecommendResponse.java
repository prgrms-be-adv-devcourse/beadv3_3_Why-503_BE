package io.why503.aiservice.domain.ai.model.dto.response;

public record RecommendResponse(
        // 추천 공연 sq
        Long showSq,
        // 추천 공연 이름
        String showName
) {
}
