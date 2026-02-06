package io.why503.reservationservice.domain.booking.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매 정보와 소속된 티켓 목록을 전달하는 응답 객체
 */
public record BookingResponse(
        @NotNull
        Long sq,
        @NotBlank
        String orderId,
        @NotBlank
        String status,
        @NotBlank
        String statusDescription,
        @NotNull
        Long originalAmount,
        @NotNull
        Long finalAmount,
        @NotBlank
        String cancelReason,
        @NotNull
        LocalDateTime createdDt,
        @NotNull
        List<TicketResponse> tickets
) {
}