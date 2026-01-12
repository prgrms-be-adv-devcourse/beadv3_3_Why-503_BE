package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.ett.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class BookingResDto {
    private Long bookingSq;
    private Long userSq;
    private Integer bookingStatus;
    private Integer bookingAmount;
    private LocalDateTime bookingDt;
    private List<Long> seatSqs; // 예매된 좌석 번호들

    // Entity -> DTO 변환 (Factory Method)
    public static BookingResDto from(Booking booking) {
        return BookingResDto.builder()
                .bookingSq(booking.getBookingSq())
                .userSq(booking.getUserSq())
                .bookingStatus(booking.getBookingStatus())
                .bookingAmount(booking.getBookingAmount())
                .bookingDt(booking.getBookingDt())
                .seatSqs(booking.getTickets().stream()
                        .map(t -> t.getShowingSeatSq())
                        .collect(Collectors.toList()))
                .build();
    }
}
