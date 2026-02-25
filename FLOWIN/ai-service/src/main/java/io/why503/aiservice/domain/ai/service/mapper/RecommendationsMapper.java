package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenreResolver;
import io.why503.aiservice.domain.ai.model.vo.AiRecommendation;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;
import io.why503.aiservice.domain.ai.service.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendationsMapper implements Recommendation {

    private final ShowGenreResolver genreResolver;

    public Recommendations toDomain(AiRecommendation aiRecommendation) {
        return toDomainOrNull(aiRecommendation);
    }

    public Recommendations toDomainOrNull(AiRecommendation aiRecommendation) {
        ShowCategory showCategory = ShowCategory.fromString(aiRecommendation.showCategory());

        ShowGenre showGenre = genreResolver.fromStringOrNull(aiRecommendation.showGenre());
        if (showGenre == null) {
            log.warn("AI genre not mapped: showCategory={}, genre={}",
                    aiRecommendation.showCategory(), aiRecommendation.showGenre());
            return null;
        }

        // category-genre 불일치 방어
        if (!showCategory.supports(showGenre)) {
            log.warn("AI genre not supported by category: category={}, genre={}",
                    showCategory.name(), showGenre.toString());
            return null;
        }

        return new Recommendations(
                showCategory.name(),
                aiRecommendation.reason(),
                showGenre.getName()
        );
    }
}