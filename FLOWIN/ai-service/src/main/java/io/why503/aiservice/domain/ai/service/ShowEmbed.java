package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;
import io.why503.aiservice.global.client.dto.response.PerformanceResponse;

import java.util.List;

public interface ShowEmbed {

    void upsert(List<PerformanceResponse> performanceResponses);
    void syncShows(ShowCategory category, ShowGenre genre);
}
