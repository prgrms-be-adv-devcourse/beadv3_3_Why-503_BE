package io.why503.aiservice.model.vo;

import io.why503.aiservice.model.embedding.Category;
import io.why503.aiservice.model.embedding.Performance;
import io.why503.aiservice.model.embedding.ShowCategory;

//공연의 종류 점수 부여 ( 최종 선정된 공연의 장르 결과 )
public record TypeShowScore(
        //장르
        Category category,
        //공연 종류
        ShowCategory showType,
        //점수
        double TypeScore,
        Performance performance
) {
        public TypeShowScore (Category category, ShowCategory showType, double TypeScore, Performance performance) {
                this.category = category;
                this.showType = showType;
                this.TypeScore = TypeScore;
                this.performance = performance;
        }
}
