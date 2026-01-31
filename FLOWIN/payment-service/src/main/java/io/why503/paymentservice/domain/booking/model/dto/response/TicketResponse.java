package io.why503.paymentservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;

public record TicketResponse(
        Long sq,
        Long roundSeatSq,
        String uuid, // 입장 확인용 QR 코드 값

        // [공연 스냅샷]
        String showName,
        String hallName,
        LocalDateTime roundDt,

        // [좌석 스냅샷]
        String seatGrade,
        String seatArea,
        Integer seatAreaNum,

        // [가격 스냅샷]
        Long originalPrice,
        String discountPolicy,            // 할인 정책 코드 (예: YOUTH)
        String discountPolicyDescription, // 할인 정책 설명 (예: 청소년 할인)
        Long discountAmount,
        Long finalPrice,

        // [상태]
        String status,            // 상태 코드 (예: PAID)
        String statusDescription  // 상태 설명 (예: 결제됨)
) {
}