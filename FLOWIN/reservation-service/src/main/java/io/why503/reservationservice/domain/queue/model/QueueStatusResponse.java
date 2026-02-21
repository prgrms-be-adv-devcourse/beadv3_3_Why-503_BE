package io.why503.reservationservice.domain.queue.model;

public record QueueStatusResponse(
        String status,              // ENTER | WAITING
        Long myPosition,            // 내 순번
        Long totalWaiting,          // 전체 대기 인원
        Long remainingTickets,      // 남은 좌석 수 (임시)
        Long avgProcessPerMinute    // 분당 처리량 (임시)
) {}
