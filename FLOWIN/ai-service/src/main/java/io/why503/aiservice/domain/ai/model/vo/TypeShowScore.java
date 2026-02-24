package io.why503.aiservice.domain.ai.model.vo;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.Performance;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;

//공연의 종류 점수 부여 ( 최종 선정된 공연의 장르 결과 )
public record TypeShowScore(
        //장르
        ShowCategory showCategory,
        //공연 종류
        ShowGenre genre,
        //점수
        double typeScore,
        //공연
        Performance performance
) { }
