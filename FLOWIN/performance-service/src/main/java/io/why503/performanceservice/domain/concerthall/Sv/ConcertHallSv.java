/**
 * Concert Hall Service
 * 공연장 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 *
 * 사용 목적 :
 * - 공연장 등록
 * - 공연장 단건 조회
 * - 공연장 등록 시 좌석 자동 생성
 *
 * 설계 의도 :
 * - Controller 와 Repository 사이의 중간 계층
 * - 구현체 교체 및 테스트 용이성 확보
 */
package io.why503.performanceservice.domain.concerthall.Sv;

import java.util.List;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.seat.Model.Dto.Cmd.SeatAreaCreateCmd;

public interface ConcertHallSv {

    /**
     * 공연장 등록 (좌석 생성 없음 - 기존 방식 유지)
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

    /**
     * 기본 좌석 자동 생성 공연장 등록
     *
     * 처리 내용 :
     * - 공연장 생성
     * - 기본 규칙에 따라 좌석 자동 생성
     *
     * @param reqDto 공연장 등록 요청 DTO
     * @return 생성된 공연장 식별자
     */
    Long createWithDefaultSeats(ConcertHallReqDto reqDto);

    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     *
     * 처리 내용 :
     * - 공연장 생성
     * - 관리자 입력 구역/좌석 수 기준 좌석 생성
     *
     * @param reqDto 공연장 등록 요청 DTO
     * @param seatAreaCmds 좌석 구역 생성 정보
     * @return 생성된 공연장 식별자
     */
    Long createWithCustomSeats(
            ConcertHallReqDto reqDto,
            List<SeatAreaCreateCmd> seatAreaCmds
    );
}
