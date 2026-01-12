package io.why503.paymentservice.domain.ticketing.model.dto;

import lombok.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketingReqDto {
    private Long userSq;            // 예매자 ID (User MSA)
    private List<TicketItem> items; // 예매할 좌석들
    private Integer totalAmount;    // 총 결제 금액

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketItem {
        private Long showingSeatSq; // 좌석 ID (Concert MSA)
        private Integer price;      // 개별 가격
    }
}