package io.why503.paymentservice.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketResDto {
    private Long ticketSq;
    private Long seatSq;
    private String status;

    // 🗑️ [삭제] from 메서드는 이제 TicketMapper로 이사 갔습니다!
}