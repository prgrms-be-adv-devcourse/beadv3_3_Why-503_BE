package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AiService {
    List<String> SimilarShows(List<TypeShowScore> topFinalShows, List<Performance> performances);
    List<ShowCategory> TopCategory(ResultRequest request, float[] userVector);
    List<ShowGenre> TopGenre(ResultRequest request, Map<ShowCategory, Double> categoryScores);
    ResultRequest Tickets(List<Booking> bookings);
    CompletableFuture<ResultResponse> getRecommendations(ResultRequest request, Long userSq , ShowCategory showCategory, ShowGenre genre);
    List<String> findSimilarShows(List<Recommendations> recommendations);
    ResultResponse fallbackRecommendation(ResultRequest request, Long userSq);
}
