package io.why503.aiservice.model.vo;

public record AiRecommendation(
        //ai에게 문자열 형식으로 보내는 추천
        /**
         * 장르
         */
        String category,
        /**
         * 추천 이유
         */
        String reason
) {
}
