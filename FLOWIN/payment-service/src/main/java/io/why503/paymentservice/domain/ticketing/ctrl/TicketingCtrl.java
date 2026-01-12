package io.why503.paymentservice.domain.ticketing.ctrl;

import io.why503.reservationservice.domain.ticket.model.dto.TicketingReqDto;
import io.why503.reservationservice.domain.ticket.model.dto.TicketingResDto;
import io.why503.reservationservice.domain.ticket.sv.TicketingSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ticketings")
@RequiredArgsConstructor
public class TicketingCtrl {

    private final TicketingSv ticketingService;

    // 예매 생성 API
    @PostMapping
    public ResponseEntity<TicketingResDto> createTicketing(@RequestBody TicketingReqDto req) {
        TicketingResDto response = ticketingService.createTicketing(req);
        return ResponseEntity.ok(response);
    }

    // 예매 상세 조회 API
    @GetMapping("/{ticketingSq}")
    public ResponseEntity<TicketingResDto> getTicketing(@PathVariable Long ticketingSq) {
        TicketingResDto response = ticketingService.getTicketing(ticketingSq);
        return ResponseEntity.ok(response);
    }

    // 예매 취소 API
    @PatchMapping("/{ticketingSq}/cancel")
    public ResponseEntity<Void> cancelTicketing(@PathVariable Long ticketingSq) {
        ticketingService.cancelTicketing(ticketingSq);
        return ResponseEntity.ok().build();
    }
}