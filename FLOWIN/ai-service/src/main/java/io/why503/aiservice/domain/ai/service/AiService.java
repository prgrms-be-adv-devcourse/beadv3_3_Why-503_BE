package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.global.client.dto.response.Booking;
import io.why503.aiservice.global.client.dto.response.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.*;

import java.util.List;
import java.util.Map;

public interface AiService {
    List<String> similarShows(List<TypeShowScore> topFinalShows, List<Performance> performances);
    List<ShowCategory> topCategory(ResultRequest request, float[] userVector);
    List<String> topGenre(ResultRequest request, Map<ShowCategory, Double> categoryScores);
    ResultRequest tickets(List<Booking> bookings);
    ResultResponse getRecommendations(Long userSq , ShowCategory showCategory, ShowGenre genre);
    List<String> findSimilarShows(List<Recommendations> recommendations);
    ResultResponse fallbackRecommendation(ResultRequest request, Long userSq);
}
