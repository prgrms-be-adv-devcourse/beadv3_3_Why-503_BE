package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.vo.BookingStat;
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
    private BookingStat bookingStat;
    private Integer bookingAmount;
    private LocalDateTime bookingDt;

    // [변경] 단순 좌석번호 리스트 -> 티켓 상세 리스트
    private List<TicketResDto> tickets;
}