package io.why503.reservationservice.domain.booking.service.impl;

import org.springframework.stereotype.Service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingEnterRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingEnterResponse;
import io.why503.reservationservice.domain.booking.service.BookingEnterService;
import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.service.QueueService;
import io.why503.reservationservice.global.client.PerformanceClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingEnterServiceImpl implements BookingEnterService {
    
    private final PerformanceClient performanceClient;
    private final QueueService queueService;
    private final EntryTokenService entryTokenService;

    @Override
    public BookingEnterResponse enter(Long userSq, BookingEnterRequest request) {
        Long roundSq = request.roundSq();

        // 1. 회차 예매 가능 여부 확인
        Boolean bookable;
        try {
            bookable = performanceClient.checkRoundBookable(roundSq);
        } catch (Exception e) {
            // performance 서비스 장애 시 fail-closed
            return BookingEnterResponse.closed();
        }

        if (!Boolean.TRUE.equals(bookable)) {
            return BookingEnterResponse.closed();
        }

        // 2. 대기열 판단
        QueueResult queueResult = queueService.tryEnter(roundSq, userSq);

        if (!queueResult.entered()) {
            return BookingEnterResponse.waiting(queueResult.position());
        }

        // 3. entryToken 발급
        String entryToken = entryTokenService.issue(userSq, roundSq);

        return BookingEnterResponse.enter(entryToken);
    }
}
