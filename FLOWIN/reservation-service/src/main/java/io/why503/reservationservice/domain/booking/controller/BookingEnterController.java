package io.why503.reservationservice.domain.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingEnterRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingEnterResponse;
import io.why503.reservationservice.domain.booking.service.BookingEnterService;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


// 예매 진입 컨트롤러 | 대기열 판단 | entryToken 발급
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingEnterController {

    private final BookingEnterService bookingEnterService;

    // 예매 진입 입구 [ 대기열 판단 후 입장 ] 
    @PostMapping("/enter")
    public ResponseEntity<BookingEnterResponse> canEnter(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid BookingEnterRequest request
    ) {
        validateUserHeader(userSq);
        return ResponseEntity.ok(
                bookingEnterService.enter(userSq, request)
        );
    }
    
    private void validateUserHeader(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest(
                "유효하지 않은 사용자 헤더(X-USER-SQ)입니다."
            );
        }
    }
}