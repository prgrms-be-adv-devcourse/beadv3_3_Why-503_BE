package io.why503.aiservice.domain.ai.model.vo;

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
        Map<String, String> categoryScore,
        //분위기점수
        Map<String, String> genreScore,
        //상위 카테고리
        List<String> topCategory,
        //상위 장르
        List<String> topGenre,
        //최종 선정된 공연 추천
        List<String> topFinalShows,
        //유사한 장르
        List<String> similarShows,
        //유사 높은 공연
        List<String> similarTopShows
) {

}
