package io.why503.reservationservice.domain.queue.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.why503.reservationservice.domain.queue.model.QueueStatusResponse;
import io.why503.reservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // 대기열 상태 조회 (UI polling 용)
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getStatus(
            @RequestParam Long roundSq,
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        return ResponseEntity.ok(
                queueService.getStatus(roundSq, userSq)
        );
    }
}
