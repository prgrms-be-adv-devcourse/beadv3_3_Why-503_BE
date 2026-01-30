package io.why503.aiservice.model.vo;

import java.util.List;

//json 형식에서 타입속성 부여된 정보 리스트 모아서 최종 응답
public record ResultResponse(
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
        List<Recommendations> recommendations
) {
}
