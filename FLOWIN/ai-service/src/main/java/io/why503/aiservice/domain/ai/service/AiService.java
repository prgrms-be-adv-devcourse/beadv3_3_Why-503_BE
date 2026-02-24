package io.why503.aiservice.domain.ai.service;

import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.*;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AiService {
    float[] embed(ResultRequest request, Long userSq);
    double cosineSimilarity(float[] vectorA, float[] vectorB);
    Map<ShowCategory, Double> CategoryScores(ResultRequest request, float[] userVector);
    Map<ShowGenre, Double> GenreScores(ResultRequest request, Map<ShowCategory, Double> categoryScores);
    double FinalScore(Performance performance, Map<ShowCategory, Double> categoryScores, Map<ShowGenre, Double> genreScores);
    List<TypeShowScore> FinalShowRanking(List<Performance> performances, Map<ShowCategory, Double> categoryScores, Map<ShowGenre, Double> genreScores);
    List<String> SimilarShows(List<TypeShowScore> topFinalShows, List<Performance> performances);
    List<ShowCategory> TopCategory(ResultRequest request, float[] userVector);
    List<ShowGenre> TopGenre(ResultRequest request, Map<ShowCategory, Double> categoryScores);
    CompletableFuture<String> ask(String prompt);
    String cleanJson(String content);
    Recommendations toDomain(AiRecommendation ar);
    Map<String, String> convertCategoryScore(Map<ShowCategory, Double> scores);
    Map<String, String> convertGenreScore(Map<ShowGenre, Double> scores);
    List<Document> searchCategoryRules(List<ShowCategory> topShowCategory);
    List<Document> searchPerformances(List<ShowCategory> topShowCategory);
    ResultRequest Tickets(List<Booking> bookings);
    CompletableFuture<ResultResponse> getRecommendations(ResultRequest request, Long userSq , ShowCategory showCategory, ShowGenre genre);
    List<String> findSimilarShows(List<Recommendations> fallbackRecommendations);
    ResultResponse fallbackRecommendation(ResultRequest request, Long userSq);
}
