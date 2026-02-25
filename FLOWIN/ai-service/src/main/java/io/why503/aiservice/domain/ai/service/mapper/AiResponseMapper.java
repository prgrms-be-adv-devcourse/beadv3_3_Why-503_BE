package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.vo.AiResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AiResponseMapper {


    public AiResponse AiResponseToFixedResponse(
            AiResponse aiResponse,
            Map<String, String> categoryScore,
            Map<String, String> genreScore,
            List<String> topCategory,
            List<String> topGenre,
            List<String> topFinalShows
    ) {

            return new AiResponse(
                    Optional.ofNullable(aiResponse.summary()).orElse(""),
                    Optional.ofNullable(aiResponse.explanations()).orElse(List.of()),
                    Optional.ofNullable(aiResponse.recommendations()).orElse(List.of()),
                    Optional.ofNullable(aiResponse.categoryScore()).orElse(categoryScore),
                    Optional.ofNullable(aiResponse.genreScore()).orElse(genreScore),
                    Optional.ofNullable(aiResponse.topCategory()).orElse(topCategory),
                    Optional.ofNullable(aiResponse.topGenre()).orElse(topGenre),
                    Optional.ofNullable(aiResponse.topFinalShows()).orElse(topFinalShows)
            );
        }
    }

