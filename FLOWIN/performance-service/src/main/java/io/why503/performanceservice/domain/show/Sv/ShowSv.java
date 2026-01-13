/**
 * Show Service
 * 공연(Show) 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 *
 * 사용 목적 :
 * - 공연 등록 비즈니스 로직 정의
 * - 공연 조회 비즈니스 로직 정의
 *
 * 설계 의도 :
 * - Controller는 요청/응답만 처리
 * - 실제 비즈니스 로직은 구현체(ShowSvImpl)에서 담당
 * - 인터페이스 기반 설계로 테스트 및 확장성 확보
 */
package io.why503.performanceservice.domain.show.Sv;

import io.why503.performanceservice.domain.show.Model.Dto.ShowReqDto;
import io.why503.performanceservice.domain.show.Model.Dto.ShowResDto;

public interface ShowSv {

    /**
     * 공연 등록
     *
     * @param reqDto 공연 등록에 필요한 요청 데이터
     * @return 등록된 공연 정보
     */
    ShowResDto createShow(ShowReqDto reqDto);

    /**
     * 공연 단건 조회
     *
     * @param showSq 조회할 공연 식별자
     * @return 공연 상세 정보
     */
    ShowResDto getShow(Long showSq);
}
