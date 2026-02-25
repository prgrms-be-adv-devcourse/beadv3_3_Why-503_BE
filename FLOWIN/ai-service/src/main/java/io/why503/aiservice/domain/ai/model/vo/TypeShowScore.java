package io.why503.aiservice.domain.ai.model.vo;

import io.why503.aiservice.global.client.dto.response.Performance;

//공연의 종류 점수 부여 ( 최종 선정된 공연의 장르 결과 )
public record TypeShowScore(
        //장르
        String showCategory,
        //공연 종류
        String genre,
        //점수
        double typeScore,
        //공연
        Performance performance
) { }
