package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long userSq;
    private Integer totalAmount;
    private Integer usedPoint;
    // 내부 클래스가 아니라, TicketDto 파일을 참조합니다.
    private List<TicketRequest> tickets;
}