/**
 * Show Service
 * 공연(Show) 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 * 사용 목적 :
 * - 공연 등록 비즈니스 로직 정의
 * - 공연 조회 비즈니스 로직 정의
 * - 공연 + 좌석 정책(show_seat) 동시 생성 비즈니스 로직 정의
 * 설계 의도 :
 * - Controller는 요청/응답만 처리
 * - 실제 비즈니스 로직은 구현체(ShowServiceImpl)에서 담당
 * - 인터페이스 기반 설계로 테스트 및 확장성 확보
 */
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
