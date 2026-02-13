package io.why503.aiservice.model.vo;

import io.why503.aiservice.model.embedding.*;

import java.util.Set;

public record Recommendations(
        // 장르타입 추가
        Category category,
        // 추천 이유
        String reason,
        //분위기타입 추가
        Set<MoodCategory> mood,
        //추천된 장르
        ShowCategory showCategory,
        String name,
        String hall,
        String startDate,
        String endDate
) {
}
