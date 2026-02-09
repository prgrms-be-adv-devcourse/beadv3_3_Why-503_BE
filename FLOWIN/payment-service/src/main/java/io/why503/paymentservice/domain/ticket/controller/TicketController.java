package io.why503.paymentservice.domain.ticket.controller;

import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 발권된 티켓의 정보 조회 및 관리를 담당하는 컨트롤러
 * - 사용자별 티켓 목록 제공 및 시스템 간 티켓 데이터 동기화 수행
 */
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // 공연 회차 생성 시점에 맞춘 티켓 기본 데이터 초기화
    @PostMapping("/init")
    public ResponseEntity<Void> createTicketSlots(@RequestBody @Valid TicketCreateRequest request) {
        ticketService.createTicketSlots(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 좌석 식별자를 통한 특정 티켓의 발급 상태 확인
    @GetMapping("/round-seat/{roundSeatSq}")
    public ResponseEntity<TicketResponse> findTicketByRoundSeat(@PathVariable Long roundSeatSq) {
        TicketResponse response = ticketService.findTicketByRoundSeat(roundSeatSq);
        return ResponseEntity.ok(response);
    }

    // 타 서비스의 요청에 따른 다건의 티켓 상세 정보 일괄 추출
    @PostMapping("/list")
    public ResponseEntity<List<TicketResponse>> findTicketsByRoundSeats(@RequestBody List<Long> roundSeatSqs) {
        List<TicketResponse> responses = ticketService.findTicketsByRoundSeats(roundSeatSqs);
        return ResponseEntity.ok(responses);
    }

    // 사용자가 보유한 모든 유효 티켓 이력 조회
    @GetMapping
    public ResponseEntity<List<TicketResponse>> findMyTickets(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);

        List<TicketResponse> responses = ticketService.findMyTickets(userSq);
        return ResponseEntity.ok(responses);
    }

    // 개별 티켓의 상세 정보 및 입장 보안 정보 조회
    @GetMapping("/{ticketSq}")
    public ResponseEntity<TicketResponse> findMyTicket(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long ticketSq) {

        validateUserHeader(userSq);

        TicketResponse response = ticketService.findMyTicket(userSq, ticketSq);
        return ResponseEntity.ok(response);
    }

    // 게이트웨이로부터 전달된 사용자 식별 정보의 유효성 검증
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}