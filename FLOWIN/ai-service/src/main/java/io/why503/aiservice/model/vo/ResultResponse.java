package io.why503.aiservice.model.vo;

import java.util.List;
import java.util.Map;

//json 형식에서 타입속성 부여된 정보 리스트 모아서 최종 응답
public record ResultResponse(
        //추천 요약 문장
        String summary,
        //설명 문장
        List<String> explanations,
        //추천 결과 정보 ( category, reason, mood, showCategory)
        List<Recommendations> recommendations,
        //장르점수
        Map<String, Double> categoryScore,
        //분위기점수
        Map<String, Double>moodScore,
        //상위 장르
        List<String> topCategory,
        //상위 분위기
        List<String> topMood,
        //최종 선정된 공연 추천
        List<String> topFinalShows,
        //유사한 장르
        List<String> similarShows
) {
}
