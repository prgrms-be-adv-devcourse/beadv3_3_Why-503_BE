/**
 * Concert Hall Service
 * 공연장 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 *
 * 사용 목적 :
 * - 공연장 등록
 * - 공연장 단건 조회
 *
 * 설계 의도 :
 * - Controller 와 Repository 사이의 중간 계층
 * - 구현체 교체 및 테스트 용이성 확보
 */
package io.why503.performanceservice.domain.concerthall.Sv;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;

public interface ConcertHallSv {

    /**
     * 공연장 등록
     *
     * @param reqDto 공연장 등록 요청 DTO
     */
    void createConcertHall(ConcertHallReqDto reqDto);

    /**
     * 공연장 단건 조회
     *
     * @param concertHallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    ConcertHallResDto getConcertHall(Long concertHallSq);
}
