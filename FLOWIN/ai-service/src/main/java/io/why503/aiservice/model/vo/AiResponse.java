package io.why503.aiservice.model.vo;

import java.util.List;

//ai용 문자열 반환하기 위한 응답
public record AiResponse(

        /**
         * 추천 요약 문장
         */
        String summary,
        /**
         * 설명 문장
         */
        List<String> explanations,
        /**
         * 추천 결과 정보
         */
        List<AiRecommendation> recommendations
) {
}
