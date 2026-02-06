package io.why503.aiservice.model.vo;

//공연의 종류 점수 부여 ( 최종 선정된 공연의 장르 결과 )
public record ScoredShow(
        //장르
        Category category,
        //공연 종류
        ShowCategory showType,
        //점수
        double TypeScore
) {}
