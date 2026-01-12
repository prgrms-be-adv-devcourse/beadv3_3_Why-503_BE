package io.why503.paymentservice.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BookingResDto {
    private Long bookingSq;
    private Long userSq;
    private Integer bookingStatus;
    private Integer bookingAmount;
    private LocalDateTime bookingDt;
    private List<Long> seatSqs;
}