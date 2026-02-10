package io.why503.performanceservice.domain.roundSeat.controller;


import io.why503.performanceservice.domain.roundSeat.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeat.model.dto.request.RoundSeatStatusRequest;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeat.service.RoundSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/round-seats")
public class RoundSeatController {

    private final RoundSeatService roundSeatService;

    // 회차 좌석 생성
    @PostMapping
    public ResponseEntity<RoundSeatResponse> createRoundSeat(
            @RequestHeader("X-USER-SQ") Long userSq, // 헤더에서 유저 SQ 주입
            @Valid @RequestBody RoundSeatRequest request
    ) {
        RoundSeatResponse response = roundSeatService.createRoundSeat(userSq, request);
        return ResponseEntity.ok(response);
    }

    // 전체 조회  /round-seats/all?roundSq=검색하고 싶은 회차시퀀스 번호
    @GetMapping("/all")
    public ResponseEntity<List<RoundSeatResponse>> getRoundSeatList(
            @RequestHeader(value = "X-USER-SQ", required = false) Long userSq,
            @RequestParam(name = "roundSq") Long roundSq) {

        return ResponseEntity.ok(roundSeatService.getRoundSeatList(userSq, roundSq));
    }

    // 예매 가능 좌석 조회
    @GetMapping("/available")
    public ResponseEntity<List<RoundSeatResponse>> getAvailableRoundSeatList(@RequestParam(name = "roundSq") Long roundSq) {
        return ResponseEntity.ok(roundSeatService.getAvailableRoundSeatList(roundSq));
    }

    // 상태 변경
    @PatchMapping("/{roundSeatSq}/status")
    public ResponseEntity<RoundSeatResponse> patchRoundSeatStatus(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable(name = "roundSeatSq") Long roundSeatSq,
            @RequestBody @Valid RoundSeatStatusRequest request
    ) {
        // request.roundSeatStatus()로 값 꺼내기
        RoundSeatResponse response = roundSeatService.patchRoundSeatStatus(userSq, roundSeatSq, request.roundSeatStatus());
        return ResponseEntity.ok(response);
    }

    // 좌석 선점
    @PostMapping("/reserve")
    public ResponseEntity<List<SeatReserveResponse>> reserveSeats(
            @RequestHeader(value = "X-USER-SQ", required = false) Long userSq,
            @RequestBody List<Long> roundSeatSqs) {
        List<SeatReserveResponse> response = roundSeatService.reserveSeats(userSq, roundSeatSqs);
        return ResponseEntity.ok(response);
    }

    // 좌석 선점 해제
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelSeats(@RequestBody List<Long> roundSeatSqs) {
        roundSeatService.releaseSeats(roundSeatSqs);
        return ResponseEntity.ok("선점이 취소되었습니다.");
    }

    // 좌석 판매 확정
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmSeats(
            @RequestHeader(value = "X-USER-SQ", required = false) Long userSq,
            @RequestBody List<Long> roundSeatSqs) {
        roundSeatService.confirmSeats(userSq, roundSeatSqs);
        return ResponseEntity.ok("판매가 확정되었습니다.");
    }

    // 좌석 정보 조회
    @PostMapping("/details")
    public ResponseEntity<List<SeatReserveResponse>> getRoundSeatDetails(
            @RequestBody List<Long> roundSeatSqs) {
        return ResponseEntity.ok(roundSeatService.getRoundSeatDetails(roundSeatSqs));
    }
}