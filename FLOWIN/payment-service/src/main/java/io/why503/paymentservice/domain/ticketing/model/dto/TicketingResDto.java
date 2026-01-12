package io.why503.paymentservice.domain.ticketing.model.dto;

import io.why503.paymentservice.domain.ticketing.model.ett.Ticket;
import io.why503.paymentservice.domain.ticketing.model.ett.Ticketing;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class TicketingResDto {
    private Long ticketingSq;
    private Long userSq;
    private Integer totalAmount;
    private Integer status; // 0:대기, 1:완료, 2:취소
    private LocalDateTime ticketingDt;
    private List<TicketResDto> tickets;

    // Entity -> DTO 변환 (생성자나 Static Method로 관리)
    public static TicketingResDto from(Ticketing entity) {
        return TicketingResDto.builder()
                .ticketingSq(entity.getTicketingSq())
                .userSq(entity.getUserSq())
                .totalAmount(entity.getTicketingPay())
                .status(entity.getTicketingStat())
                .ticketingDt(entity.getTicketingDt())
                .tickets(entity.getTickets().stream()
                        .map(TicketResDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TicketResDto {
        private Long ticketSq;
        private String ticketNo;
        private Integer price;
        private Long showingSeatSq;
        private Integer status;

        public static TicketResDto from(Ticket entity) {
            return TicketResDto.builder()
                    .ticketSq(entity.getTicketSq())
                    .ticketNo(entity.getTicketNo())
                    .price(entity.getTicketPrice())
                    .showingSeatSq(entity.getShowingSeatSq())
                    .status(entity.getTicketStat())
                    .build();
        }
    }
}