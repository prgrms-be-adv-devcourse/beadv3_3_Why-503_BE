package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.service.ScoreResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScoreResponseMapper implements ScoreResponse {
    //점수 반환
    public Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry ->  String.format("%.2f", entry.getValue()),
                        (a, b) -> b,
                        () -> new LinkedHashMap<String, String>()
                        //정렬 후 변환 때 순서 유지
                ));
    }

    //점수 반환
    public Map<String, String> convertGenreScore(Map<ShowGenre, Double> scores) {
        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> String.format("%.2f", entry.getValue())
                ));
    }
}
