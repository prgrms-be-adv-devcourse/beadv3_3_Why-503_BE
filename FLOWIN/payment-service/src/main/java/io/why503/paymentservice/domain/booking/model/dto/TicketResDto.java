package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TicketResDto {
    private Long ticketSq;
    private Long seatSq;   // 좌석 번호 (showingSeatSq)
    private String status; // 상태 설명 (예: "결제됨", "취소됨")

    // Entity -> DTO 변환 편의 메서드
    public static TicketResDto from(Ticket ticket) {
        return TicketResDto.builder()
                .ticketSq(ticket.getTicketSq())
                .seatSq(ticket.getShowingSeatSq())
                // Enum의 한글 설명을 꺼내서 담습니다 (예: "취소됨")
                .status(ticket.getTicketStatus().getDescription())
                .build();
    }
}