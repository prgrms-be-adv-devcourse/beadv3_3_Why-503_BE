package io.why503.reservationservice.domain.booking.mapper;

import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;
import io.why503.reservationservice.domain.booking.model.entity.BookingSeat;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 예매 엔티티와 DTO 간의 데이터 변환을 담당하는 컴포넌트
 * - 엔티티 구조를 클라이언트 응답 형식에 맞춰 가공 및 변환
 */
@Component
public class BookingMapper {

    // 도메인 모델을 외부 노출용 데이터 객체로 변환
    public BookingResponse entityToResponse(Booking booking) {
        if (booking == null) {
            throw BookingExceptionFactory.bookingBadRequest("변환할 Booking Entity는 필수입니다.");
        }

        List<Long> roundSeatSqs = extractRoundSeatSqs(booking.getBookingSeats());

        return new BookingResponse(
                booking.getSq(),
                booking.getUserSq(),
                booking.getOrderId(),
                booking.getStatus().name(),
                roundSeatSqs,
                booking.getCreatedDt()
        );
    }

    // 예매 정보에 포함된 개별 좌석들의 식별자만 추출
    private List<Long> extractRoundSeatSqs(List<BookingSeat> bookingSeats) {
        if (bookingSeats == null || bookingSeats.isEmpty()) {
            return Collections.emptyList();
        }

        return bookingSeats.stream()
                .map(seat -> seat.getRoundSeatSq())
                .toList();
    }
}