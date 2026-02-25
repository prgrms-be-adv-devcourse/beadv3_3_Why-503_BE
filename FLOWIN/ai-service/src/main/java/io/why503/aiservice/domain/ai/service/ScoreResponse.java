package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;

import java.util.Map;

public interface ScoreResponse {
    Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores);
    Map<String, String> convertGenreScore(Map<ShowGenre, Double> scores);
}
