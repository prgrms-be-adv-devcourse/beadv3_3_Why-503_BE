package io.why503.performanceservice.domain.roundSeats.controller;


import io.why503.performanceservice.domain.roundSeats.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.model.dto.request.RoundSeatStatusRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeats.service.RoundSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // GlobalExceptionHandler에 넣을 내용들, 지금은 충돌이 일어날 수 있어서 컨트롤러에 작성함
    //동시성 이슈 (낙관적 락 충돌)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockException(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("선택한 좌석 중 이미 예약된 좌석이 포함되어 있습니다. (새로고침 후 다시 시도해주세요)");
    }

    //비즈니스 로직 에러 (이미 판매됨, 상태 오류 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    //잘못된 요청 데이터 (존재하지 않는 회차 ID 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    //데이터 중복 생성 (이미 등록된 좌석 또 생성 시)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("DB 에러 상세: " + e.getRootCause().getMessage());
    }

   // @Valid 유효성 검사 실패 (@NotNull 위반 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}