package io.why503.paymentservice.domain.booking.model.dto.response;

import java.time.LocalDateTime;

/**
 * 개별 티켓의 상세 정보와 스냅샷 데이터를 전달하는 응답 객체
 */
public record TicketResponse(
        Long sq,
        Long roundSeatSq,
        String uuid,
        String showName,
        String hallName,
        LocalDateTime roundDt,
        String seatGrade,
        String seatArea,
        Integer seatAreaNum,
        Long originalPrice,
        String discountPolicy,
        String discountPolicyDescription,
        Long discountAmount,
        Long finalPrice,
        String status,
        String statusDescription
) {
}