package io.why503.paymentservice.domain.booking.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 개별 티켓의 상세 정보와 스냅샷 데이터를 전달하는 응답 객체
 */
public record TicketResponse(
        @NotNull
        Long sq,
        @NotNull
        Long roundSeatSq,
        @NotBlank
        String uuid,
        @NotBlank
        String showName,
        @NotBlank
        String hallName,
        @NotNull
        LocalDateTime roundDt,
        @NotBlank
        String seatGrade,
        @NotBlank
        String seatArea,
        @NotNull
        Integer seatAreaNum,
        @NotNull
        Long originalPrice,
        @NotBlank
        String discountPolicy,
        @NotBlank
        String discountPolicyDescription,
        @NotNull
        Long discountAmount,
        @NotNull
        Long finalPrice,
        @NotBlank
        String status,
        @NotBlank
        String statusDescription
) {
}