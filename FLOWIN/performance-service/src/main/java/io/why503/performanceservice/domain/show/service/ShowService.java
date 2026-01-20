/**
 * Show Service
 * 공연(Show) 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 *
 * 사용 목적 :
 * - 공연 등록 비즈니스 로직 정의
 * - 공연 조회 비즈니스 로직 정의
 * - 공연 + 좌석 정책(show_seat) 동시 생성 비즈니스 로직 정의
 *
 * 설계 의도 :
 * - Controller는 요청/응답만 처리
 * - 실제 비즈니스 로직은 구현체(ShowSvImpl)에서 담당
 * - 인터페이스 기반 설계로 테스트 및 확장성 확보
 */
package io.why503.performanceservice.domain.show.service;

import io.why503.performanceservice.domain.show.model.dto.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowResponse;

public interface ShowService {

    /**
     * 공연 + 좌석 정책 생성
     * (COMPANY 권한 필수)
     */
    Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest req,
            String authorization
    );

    /**
     * 공연 단독 생성
     * (COMPANY 권한 필수)
     */
    ShowResponse createShow(
            ShowRequest req,
            String authorization
    );

    /**
     * 공연 단건 조회
     */
    ShowResponse getShow(Long showSq);
}
