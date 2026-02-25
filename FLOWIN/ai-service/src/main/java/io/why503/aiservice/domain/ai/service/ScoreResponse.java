package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;

import java.util.Map;

public interface ScoreResponse {
    Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores);
    Map<String, String> convertGenreScore(Map<String, Double> scores);
}
