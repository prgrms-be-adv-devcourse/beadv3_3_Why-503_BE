package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.vo.AiResponse;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;
import io.why503.aiservice.domain.ai.model.vo.ResultResponse;

import java.util.List;

public class ResultResponseMapper {

    public static ResultResponse toResultResponse(
            AiResponse fixedResponse,
            List<Recommendations> finalRecommendations,
            List<String> similarShows,
            List<String> similarTopShows
    ) {

        return new ResultResponse(
                fixedResponse.summary(),
                fixedResponse.explanations(),
                finalRecommendations,
                fixedResponse.categoryScore(),
                fixedResponse.genreScore(),
                fixedResponse.topCategory(),
                fixedResponse.topGenre(),
                fixedResponse.topFinalShows(),
                similarShows,
                similarTopShows
        );
    }
}