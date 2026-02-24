package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.vo.Recommendations;
import io.why503.aiservice.domain.ai.model.vo.ResultResponse;

import java.util.List;
import java.util.Map;

public class FallbackResultResponseMapper {

    public static ResultResponse toResultResponse(
            List<Recommendations> fallbackRecommendations,
            Map<String, String> categoryScore,
            Map<String, String> genreScore,
            List<String> topCategory,
            List<String> topGenre,
            List<String> topFinalShows,
            List<String> similarShows,
            List<String> similarTopShows
    ) {
        return new ResultResponse(
                "기본 추천 결과입니다.",
                List.of("AI 응답 실패로 기본 추천을 제공합니다."),
                fallbackRecommendations,
                categoryScore,
                genreScore,
                topCategory,
                topGenre,
                topFinalShows,
                similarShows,
                similarTopShows
        );
    }
}