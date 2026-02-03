package io.why503.aiservice.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

//json 이외에도 다양한 정보 출력
@JsonIgnoreProperties(ignoreUnknown = true)
//ai용 문자열 반환하기 위한 응답
public record AiResponse(
        //추천 요약 문장
        String summary,
        //설명 문장
        List<String> explanations,
        //추천 결과 정보
        List<AiRecommendation> recommendations,
        Map<String, Double>categoryScore,
        Map<String, Double>moodScore,
        List<String> topCategory,
        List<String> topMood
) {
}
