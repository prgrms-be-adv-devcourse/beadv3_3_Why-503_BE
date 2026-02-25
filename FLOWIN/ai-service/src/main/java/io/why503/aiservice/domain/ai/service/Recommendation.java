package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.vo.AiRecommendation;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;

public interface Recommendation {
    Recommendations toDomain(AiRecommendation ar);

}
