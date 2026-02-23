package io.why503.paymentservice.global.client;

import io.why503.paymentservice.global.client.dto.request.SeatReserveRequest;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공연 서비스와 통신하여 좌석의 선점, 취소 및 예매 확정을 처리하는 클라이언트
 */
@FeignClient(name = "performance-service")
public interface PerformanceClient {

    // 예매 시작 시 선택한 좌석들에 대한 임시 선점 요청
    @PostMapping("/round-seats/reserve")
    List<RoundSeatResponse> reserveRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    // 선점 기한 만료 또는 예매 취소 시 좌석 선점 해제 요청
    @PostMapping("/round-seats/cancel")
    void cancelRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    // 결제 완료 시 선점된 좌석을 최종 예매 확정 상태로 변경
    @PostMapping("/round-seats/confirm")
    void confirmRoundSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody SeatReserveRequest request
    );

    @PostMapping("/round-seats/details")
    List<RoundSeatResponse> findRoundSeats(
            @RequestBody SeatReserveRequest request
    );

    // 정산 처리를 위한 특정 공연(showSq)의 모든 회차 좌석 식별자(roundSeatSq) 목록 조회
    @GetMapping("/round-seats/shows/{showSq}/sqs")
    List<Long> getRoundSeatSqsByShowSq(@PathVariable("showSq") Long showSq);
}