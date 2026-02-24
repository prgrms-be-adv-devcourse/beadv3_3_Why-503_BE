package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.vo.AiRecommendation;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;

import java.util.List;

public class RecommendationsMapper {

    public static List<Recommendations> toAiRecommendations(List<AiRecommendation> aiRecommendations) {
        return aiRecommendations.stream()
                .map(ar -> {
                    //ShowCategory 변환
                    ShowCategory showCategoryEnum = parseShowCategory(ar.showCategory());

                    //ShowGenre 변환 (카테고리에 맞는 Enum 타입으로)
                    ShowGenre showGenreEnum = parseShowGenre(ar.showGenre(), showCategoryEnum);

                    return new Recommendations(
                            showCategoryEnum,
                            ar.reason(),
                            showGenreEnum
                    );
                })
                .toList();
    }

    private static ShowCategory parseShowCategory(String category) {
        return ShowCategory.valueOf(category.toUpperCase()); // 알 수 없는 값이면 IllegalArgumentException
    }

    private static ShowGenre parseShowGenre(String genre, ShowCategory category) {
        try {

            if (category == ShowCategory.CLASSIC) {
                return ClassicType.fromString(genre);
            }
            if (category == ShowCategory.CONCERT) {
                return ConcertType.fromString(genre);
            }
            if (category == ShowCategory.MUSICAL) {
                return MusicalType.fromString(genre);
            }
            if (category == ShowCategory.PLAY) {
                return PlayType.fromString(genre);
            }


            return null;
        } catch (Exception e) {
            return null;
        }
    }
}


