package io.why503.performanceservice.domain.roundSeats.service;


import io.why503.performanceservice.domain.roundSeats.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;

import java.util.List;

public interface RoundSeatService {

    //회차 좌석 생성
    //권한 검증 후 특정 회차에 속한 좌석 생성
    RoundSeatResponse createRoundSeat(Long userSq, RoundSeatRequest request);

    //특정 회차의 전체 좌석 조회
    List<RoundSeatResponse> getRoundSeatList(Long userSq, Long roundSq);

    //예매 가능 좌석 조회
    List<RoundSeatResponse> getAvailableRoundSeatList(Long roundSq);

    //좌석 상태 변경
    //권한 검증
    RoundSeatResponse patchRoundSeatStatus(Long userSq, Long roundSeatSq, RoundSeatStatus newStatus);

    //좌석 선점
    List<SeatReserveResponse> reserveSeats(Long userSq, List<Long> roundSeatSqs);

    //선점 해제
    void releaseSeats(List<Long> roundSeatSqs);

    //판매 확정
    void confirmSeats(Long userSq, List<Long> roundSeatSqs);
}
