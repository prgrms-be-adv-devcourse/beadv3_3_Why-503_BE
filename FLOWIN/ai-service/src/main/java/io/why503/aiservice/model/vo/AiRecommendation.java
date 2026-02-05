package io.why503.aiservice.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
//ai에게 문자열 형식으로 보내는 추천
public record AiRecommendation(
        //장르
        String category,
        //추천 이유
        String reason,
        String mood,
        List<String> explanations
) {

}
