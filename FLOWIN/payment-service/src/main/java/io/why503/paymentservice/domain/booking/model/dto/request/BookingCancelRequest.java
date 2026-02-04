package io.why503.paymentservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 예매 취소 요청 시 취소할 티켓 목록과 사유를 담는 객체
 */
public record BookingCancelRequest(
        @NotEmpty(message = "티켓")
        List<Long> ticketSqs,
        @NotBlank(message = "사유")
        String reason
) {}