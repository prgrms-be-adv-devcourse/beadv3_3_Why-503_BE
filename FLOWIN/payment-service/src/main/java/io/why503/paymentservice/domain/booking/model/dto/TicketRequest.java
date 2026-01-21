package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

/**
 * 티켓 생성 요청 DTO
 * - 단일 좌석 정보를 담고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private Long roundSeatSq; // 공연 회차별 좌석 ID
}