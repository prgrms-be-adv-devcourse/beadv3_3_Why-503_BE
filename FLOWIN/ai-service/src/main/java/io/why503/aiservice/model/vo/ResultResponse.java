package io.why503.aiservice.model.vo;

import java.util.List;


public record ResultResponse(
        String summary,
        List<String> explanations,
        List<Recommendations> recommendations
) {
}
