package io.why503.reservationservice.Domain.Booking.Model.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookingRequestDto {
    private Long userSq;
    private Long showingSq;      // 빠른 예매 시 필요
    private Long seatSq;         // 직접 예매 시 필요
    private String grade;        // 빠른 예매 시 필요 ("SS", "S", "R")
}