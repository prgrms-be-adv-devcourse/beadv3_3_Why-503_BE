package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 티켓 API 컨트롤러
 * - 입장 처리 등 티켓 자체에 대한 기능을 제공합니다.
 */
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final BookingService bookingService;

    // QR 입장 처리
    @PostMapping("/entry")
    public ResponseEntity<Void> enterTicket(@RequestBody String ticketUuid) {
        // JSON 문자열로 넘어올 경우 따옴표 제거 처리
        String cleanUuid = ticketUuid.replace("\"", "");
        bookingService.enterTicket(cleanUuid);
        return ResponseEntity.ok().build();
    }
}