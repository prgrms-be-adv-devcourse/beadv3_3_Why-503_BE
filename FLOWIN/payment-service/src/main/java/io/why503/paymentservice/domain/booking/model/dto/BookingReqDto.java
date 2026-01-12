package io.why503.paymentservice.domain.booking.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class BookingReqDto {
    private Long userSq;
    private Integer totalAmount; // 총 예매 금액
    private List<TicketDto> tickets; // 선택한 좌석 목록

    @Getter
    @NoArgsConstructor
    public static class TicketDto {
        private Long showingSeatSq;
        private Integer originalPrice;
        private Integer finalPrice;
    }
}