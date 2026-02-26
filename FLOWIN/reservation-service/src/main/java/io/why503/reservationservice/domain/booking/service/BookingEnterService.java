package io.why503.reservationservice.domain.booking.service;

import io.why503.reservationservice.domain.booking.model.dto.request.BookingEnterRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingEnterResponse;

public interface BookingEnterService{
    // 예매 페이지 진입 요청
    BookingEnterResponse enter(Long userSq, BookingEnterRequest request);
}
