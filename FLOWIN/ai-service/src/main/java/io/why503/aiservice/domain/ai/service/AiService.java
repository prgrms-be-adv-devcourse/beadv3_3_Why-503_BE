package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.dto.response.RecommendResponse;

import java.util.List;

public interface AiService {
    List<RecommendResponse> getRecommendations(Long userSq);
}
