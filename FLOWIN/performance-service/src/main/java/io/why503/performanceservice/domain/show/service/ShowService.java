package io.why503.performanceservice.domain.show.service;

import io.why503.performanceservice.domain.show.model.dto.request.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowGenre;

import java.util.List;

public interface ShowService {

    //공연 + 좌석 정책 생성(COMPANY 권한 필수)
    Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest request,
            Long userSq
    );

    //공연 단독 생성(COMPANY 권한 필수)
    ShowResponse createShow(
            ShowRequest request,
            Long userSq
    );

    //공연 단건 조회
    ShowResponse readShowBySq(Long showSq);

    //공연 단건 조회(round 생성용)
    ShowEntity findShowBySq(Long showSq);

    //카테고리 조회
    List<ShowResponse> findShowsByCategory(ShowCategory category);
    //카테고리+장르 조회
    List<ShowResponse> findShowsByCategoryAndGenre(ShowCategory category, ShowGenre genre);
}
