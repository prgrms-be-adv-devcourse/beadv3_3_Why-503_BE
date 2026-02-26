package io.why503.reservationservice.domain.booking.service.impl;

import org.springframework.stereotype.Service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingEnterRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingEnterResponse;
import io.why503.reservationservice.domain.booking.service.BookingEnterService;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import io.why503.reservationservice.domain.entry.util.EntryTokenExceptionFactory;
import io.why503.reservationservice.domain.queue.model.QueueResult;
import io.why503.reservationservice.domain.queue.service.QueueService;
import io.why503.reservationservice.domain.queue.util.QueueExceptionFactory;
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

        // 0. 입력 검증
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("유효하지 않은 사용자입니다.");
        }
        if (request == null || request.roundSq() == null || request.roundSq() <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("회차 정보(roundSq)가 유효하지 않습니다.");
        }

        Long roundSq = request.roundSq();

        // 1. 회차 예매 가능 여부 확인
        Boolean bookable;
        try {
            bookable = performanceClient.checkRoundBookable(roundSq);
        } catch (Exception e) {
            // 장애 시 fail-closed 대신 예외로 통일
            throw BookingExceptionFactory.bookingBadRequest("공연 회차 예매 가능 여부 조회에 실패했습니다.");
        }

        if (!Boolean.TRUE.equals(bookable)) {
            return BookingEnterResponse.closed();
        }

        // 2. 대기열 판단
        QueueResult queueResult;
        try {
            queueResult = queueService.tryEnter(roundSq, userSq);
        } catch (Exception e) {
            throw QueueExceptionFactory.queueBadRequest("대기열 처리 중 오류가 발생했습니다.");
        }

        if (queueResult == null) {
            throw QueueExceptionFactory.queueBadRequest("대기열 처리 결과가 비정상입니다.");
        }

        if (!queueResult.entered()) {
            // position null 방어
            Long pos = queueResult.position();
            if (pos == null || pos < 0) {
                throw QueueExceptionFactory.queueBadRequest("대기열 순번 계산 결과가 비정상입니다.");
            }
            return BookingEnterResponse.waiting(pos);
        }

        // 3. entryToken 발급
        String entryToken;
        try {
            entryToken = entryTokenService.issue(userSq, roundSq);
        } catch (Exception e) {
            throw EntryTokenExceptionFactory.entryTokenBadRequest("예매 권한 토큰 발급에 실패했습니다.");
        }

        if (entryToken == null || entryToken.isBlank()) {
            throw EntryTokenExceptionFactory.entryTokenBadRequest("예매 권한 토큰 발급 결과가 비정상입니다.");
        }

        return BookingEnterResponse.enter(entryToken);
    }
}