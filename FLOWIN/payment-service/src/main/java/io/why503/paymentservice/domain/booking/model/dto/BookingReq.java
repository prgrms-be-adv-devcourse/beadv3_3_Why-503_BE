package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingReq {
    private Long userSq;
    private Integer totalAmount;

    // 내부 클래스가 아니라, TicketDto 파일을 참조합니다.
    private List<TicketReq> tickets;
}