package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.AiResponse;
import io.why503.aiservice.domain.ai.model.vo.TypeShowScore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AiResponseMapper {

    //점수 반환
    public static Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry ->  String.format("%.2f...", entry.getValue())
                ));
    }

    //점수 반환
    public static Map<String, String> convertGenreScore(Map<ShowGenre, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> String.format("%.2f...", entry.getValue())
                ));
    }

    public static AiResponse toFixedResponse(
            AiResponse aiResponse,
            Map<ShowCategory, Double> categoryScores,
            Map<ShowGenre, Double> genreScores,
            List<ShowCategory> topShowCategory,
            List<ShowGenre> topGenre,
            List<TypeShowScore> finalShows
    ) {
        return new AiResponse(
                Optional.ofNullable(aiResponse.summary()).orElse(""),
                Optional.ofNullable(aiResponse.explanations()).orElse(List.of()),
                Optional.ofNullable(aiResponse.recommendations()).orElse(List.of()),
                Optional.ofNullable(aiResponse.categoryScore()).orElse(convertCategoryScore(categoryScores)),
                Optional.ofNullable(aiResponse.genreScore()).orElse(convertGenreScore(genreScores)),
                Optional.ofNullable(aiResponse.topCategory())
                        .orElse(topShowCategory.stream().map(ShowCategory::name).toList()),
                Optional.ofNullable(aiResponse.topGenre())
                        .orElse(topGenre.stream().map(ShowGenre::getName).toList()),
                Optional.ofNullable(aiResponse.topFinalShows())
                        .orElse(finalShows.stream().map(ts -> String.valueOf(ts.typeScore())).toList())
        );
    }
}
