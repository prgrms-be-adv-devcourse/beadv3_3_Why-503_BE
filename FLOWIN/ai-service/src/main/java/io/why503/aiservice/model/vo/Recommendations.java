package io.why503.aiservice.model.vo;


public record Recommendations(
        // 장르타입 추가
        Category category,
        // 추천 이유
        String reason,
        //분위기타입 추가
        MoodCategory mood,
        //추천된 장르
        ShowCategory showCategory
) {
}
