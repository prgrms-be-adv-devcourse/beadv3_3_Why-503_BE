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
 * 티켓 슬롯 관리 및 조회를 위한 컨트롤러
 * - Internal: 공연 서비스 등 내부 시스템 호출
 * - User: 일반 사용자의 티켓 조회 요청 처리
 */
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ==========================================
    // [Internal] 시스템 내부 호출용 (생성, 조회)
    // ==========================================

    /**
     * 티켓 슬롯 일괄 생성 (초기화)
     * - Performance Service에서 회차/좌석 생성 시 호출
     */
    @PostMapping("/init")
    public ResponseEntity<Void> createTicketSlots(@RequestBody @Valid TicketCreateRequest request) {
        ticketService.createTicketSlots(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 특정 좌석 ID로 티켓 단건 조회 (시스템용)
     */
    @GetMapping("/round-seat/{roundSeatSq}")
    public ResponseEntity<TicketResponse> findTicketByRoundSeat(@PathVariable Long roundSeatSq) {
        TicketResponse response = ticketService.findTicketByRoundSeat(roundSeatSq);
        return ResponseEntity.ok(response);
    }

    /**
     * 여러 좌석 ID로 티켓 목록 일괄 조회 (시스템용)
     * - 예매 서비스 등에서 사용
     */
    @PostMapping("/list")
    public ResponseEntity<List<TicketResponse>> findTicketsByRoundSeats(@RequestBody List<Long> roundSeatSqs) {
        List<TicketResponse> responses = ticketService.findTicketsByRoundSeats(roundSeatSqs);
        return ResponseEntity.ok(responses);
    }

    // ==========================================
    // [User] 사용자 요청용
    // ==========================================

    /**
     * 내 티켓 목록 전체 조회
     * - 마이페이지 등에서 사용
     */
    @GetMapping
    public ResponseEntity<List<TicketResponse>> findMyTickets(
            @RequestHeader("X-USER-SQ") Long userSq) {

        validateUserHeader(userSq);

        List<TicketResponse> responses = ticketService.findMyTickets(userSq);
        return ResponseEntity.ok(responses);
    }

    /**
     * 내 티켓 상세 조회
     * - 티켓 클릭 시 또는 QR 보기 등
     */
    @GetMapping("/{ticketSq}")
    public ResponseEntity<TicketResponse> findMyTicket(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long ticketSq) {

        validateUserHeader(userSq);

        TicketResponse response = ticketService.findMyTicket(userSq, ticketSq);
        return ResponseEntity.ok(response);
    }

    // 헤더 검증 (Gateway에서 넘어오지만 더블 체크)
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }
    }
}