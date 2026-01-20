package io.why503.paymentservice.domain.booking.controller;

import io.why503.paymentservice.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final BookingService bookingService;

    // QR 코드 스캔 시 호출될 API
    @PostMapping("/entry")
    public ResponseEntity<Void> enterTicket(@RequestBody String ticketUuid) {
        bookingService.enterTicket(ticketUuid.replace("\"", "")); // 따옴표 제거 안전장치
        return ResponseEntity.ok().build();
    }
}