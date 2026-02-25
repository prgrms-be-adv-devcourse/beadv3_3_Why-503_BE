package io.why503.aiservice.domain.ai.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
//ai에게 문자열 형식으로 보내는 추천
public record AiRecommendation(
        //장르
        String category,
        //추천 이유
        String reason,
        //추가 설명들
        List<String> explanations,
        //공연 카테고리
        String showCategory,
        //공연 장르
        String showGenre

) {

}
