package io.why503.aiservice.domain.ai.service.mapper;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.ClassicType;
import io.why503.aiservice.domain.ai.model.embedding.genre.ShowGenre;
import io.why503.aiservice.domain.ai.model.vo.AiRecommendation;
import io.why503.aiservice.domain.ai.model.vo.Recommendations;

import java.util.List;

public class RecommendationsMapper {

    public static List<Recommendations> toDomain(List<AiRecommendation> aiRecommendations) {
        return aiRecommendations.stream()
                .map(ar -> {
                    // 1️⃣ ShowCategory 변환
                    ShowCategory showCategoryEnum = parseShowCategory(ar.showCategory());

                    // 2️⃣ ShowGenre 변환 (카테고리에 맞는 Enum 타입으로)
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
            // 현재 ClassicType 예시 기준
            if (category == ShowCategory.CLASSIC) {
                return ClassicType.fromString(genre);
            }

            // 다른 카테고리별 enum도 여기에 추가 가능
            // 예: MUSICAL → MusicalType.fromString(genre)
            // CONCERT → ConcertType.fromString(genre)

            return null; // 알 수 없는 장르는 null
        } catch (Exception e) {
            return null; // fallback 처리
        }
    }
}


