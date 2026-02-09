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
 * - 엔티티의 복잡한 연관관계(BookingSeat)를 단순화하여 클라이언트에 반환
 */
@Component
public class BookingMapper {

    /**
     * Booking 엔티티 -> BookingResponse 변환
     */
    public BookingResponse entityToResponse(Booking booking) {
        if (booking == null) {
            throw BookingExceptionFactory.bookingBadRequest("변환할 Booking Entity는 필수입니다.");
        }

        // 하위 좌석 정보 추출 (Entity -> List<Long>)
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

    /**
     * BookingSeat 리스트에서 회차 좌석 ID 목록 추출
     * - 메서드 참조(::) 금지 규칙 준수
     */
    private List<Long> extractRoundSeatSqs(List<BookingSeat> bookingSeats) {
        if (bookingSeats == null || bookingSeats.isEmpty()) {
            return Collections.emptyList();
        }

        return bookingSeats.stream()
                .map(seat -> seat.getRoundSeatSq())
                .toList();
    }
}