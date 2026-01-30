package io.why503.aiservice.model.vo;

import java.util.List;


public record AiResponse(
        String summary,
        List<String> explanations,
        List<AiRecommendation> recommendations
) {
}
