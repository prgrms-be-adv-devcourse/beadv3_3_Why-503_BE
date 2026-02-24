package io.why503.aiservice.global.client.dto.request;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;

public record PerformanceRequest(
        ShowCategory category,
        ShowGenre genre
) {
}
