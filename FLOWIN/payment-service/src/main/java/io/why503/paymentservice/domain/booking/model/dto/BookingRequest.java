package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

import java.util.List;

/**
 * 예매 생성 요청 DTO
 * - 사용자가 선택한 좌석 목록과 사용할 포인트를 전달받습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    private Integer usedPoint;           // 사용 포인트 (Null 가능)
    private List<TicketRequest> tickets; // 예매할 티켓(좌석) 목록
}