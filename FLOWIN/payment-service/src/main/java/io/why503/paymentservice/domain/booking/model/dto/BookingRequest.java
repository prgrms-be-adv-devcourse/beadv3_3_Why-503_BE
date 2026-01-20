package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Integer usedPoint;           // 사용자가 입력한 포인트 (0일 수도 있음)
    private List<TicketRequest> tickets; // 어떤 좌석을 예매할지 목록
}