package io.why503.reservationservice.global.client;

import io.why503.reservationservice.global.client.dto.request.SeatReserveRequest;
import io.why503.reservationservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공연 서비스와 통신하여 실시간 좌석 가용 상태를 동기화하고 점유 권한을 제어하는 클라이언트
 */
@FeignClient(name = "performance-service")
public interface PerformanceClient {

    // >>>>>>>>예메 페이지 진입 이전>>>>>>>>>>>>>

    // 회차 예매 가능 여부 확인
    @GetMapping("/round/{roundSq}/bookable")
    Boolean checkRoundBookable(
                @PathVariable("roundSq") Long roundSq
    );


    // >>>>>>>>예매 페이지 진입 이후>>>>>>>>>>>>>

    // 특정 사용자를 위한 좌석 점유권 확보 요청
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    // 결제 미이행 또는 예매 철회 시 점유 중인 좌석 자원을 다시 판매 가능 상태로 방출
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    // 결제 승인 완료에 따른 좌석의 최종 소유권 확정 및 판매 완료 처리
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    // 다건의 좌석 식별자를 기반으로 공연 정보 및 가격 상세 데이터 추출
    @PostMapping("/round-seats/details")
    List<RoundSeatResponse> findRoundSeats(@RequestBody SeatReserveRequest request);
}