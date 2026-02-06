package io.why503.aiservice.model.vo;

//공연의 종류 점수 부여
public record ScoredShow(
        //장르
        Category category,
        //공연 종류
        ShowCategory showType,
        //점수
        double TypeScore
) {}
