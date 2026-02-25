package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.vo.AiRecommendation;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;
import io.why503.aiservice.domain.ai.service.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RecommendationsMapper implements Recommendation {

    private final ShowGenreResolver genreResolver;

    public Recommendations toDomain(AiRecommendation aiRecommendation) {
        ShowCategory showCategory =
                ShowCategory.fromString(aiRecommendation.showCategory());

        ShowGenre showGenre =
                genreResolver.fromString(aiRecommendation.showGenre());

        return new Recommendations(
                showCategory.name(),
                aiRecommendation.reason(),
                showGenre.getName()
        );
    }
}


