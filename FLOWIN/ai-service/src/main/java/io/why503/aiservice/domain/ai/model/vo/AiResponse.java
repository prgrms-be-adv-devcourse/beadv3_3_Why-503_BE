package io.why503.aiservice.domain.ai.model.vo;

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
        //장르 점수
        Map<String, String>categoryScore,
        //분위기 점수
        Map<String, String>genreScore,
        //상위 카테고리
        List<String> topCategory,
        //상위 장르
        List<String> topGenre,
        //선정된 장르 추천
        List<String> topFinalShows
) {
}
