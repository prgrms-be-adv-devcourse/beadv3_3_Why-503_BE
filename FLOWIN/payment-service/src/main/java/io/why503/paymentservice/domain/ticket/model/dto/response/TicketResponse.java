package io.why503.paymentservice.domain.ticket.model.dto.response;

import java.time.LocalDateTime;

/**
 * 티켓 슬롯의 현재 상태(판매 여부, 가격 등)를 조회하기 위한 응답 DTO
 * - 내부 로직 없이 데이터 전달만 담당 (Record)
 */
public record TicketResponse(
        Long sq,
        Long roundSeatSq,
        Long bookingSq,        // 연결된 예매 ID (없으면 null)
        Long userSq,           // 구매자 ID (없으면 null)
        String status,         // "AVAILABLE"(공석) 또는 "SOLD"(판매됨) - Mapper에서 결정
        Long originalPrice,    // 정가
        String discountPolicy, // 적용된 할인 정책
        Long finalPrice,       // 최종 결제 금액
        LocalDateTime createdDt
) {
}