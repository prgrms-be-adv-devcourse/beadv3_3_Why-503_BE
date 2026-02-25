package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.global.client.dto.response.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import io.why503.aiservice.domain.ai.model.vo.TypeShowScore;

import java.util.List;
import java.util.Map;

public interface ShowCalculator {
    double cosineSimilarity(float[] vectorA, float[] vectorB);
    Map<ShowCategory, Double> categoryScores(ResultRequest request, float[] userVector);
    Map<ShowGenre, Double> genreScores(ResultRequest request, Map<ShowCategory, Double> categoryScores);
    double finalScore(Performance performance, Map<ShowCategory, Double> categoryScores, Map<ShowGenre, Double> genreScores);
    List<TypeShowScore> finalShowRanking(
            List<Performance> performances,
            Map<ShowCategory, Double> categoryScores,
            Map<ShowGenre, Double> genreScores
    );
}
