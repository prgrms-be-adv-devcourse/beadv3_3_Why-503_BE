package io.why503.paymentservice.domain.ticket.model.dto.response;

import java.time.LocalDateTime;

/**
 * 티켓의 판매 상태 및 가격 상세 정보를 제공하는 응답 객체
 * - 개별 좌석에 할당된 티켓의 식별 정보와 결제 금액을 포함
 */
public record TicketResponse(
        Long sq,
        Long roundSeatSq,
        Long bookingSq,
        Long userSq,
        String status,
        Long originalPrice,
        String discountPolicy,
        Long finalPrice,
        LocalDateTime createdDt
) {
}