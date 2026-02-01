package io.why503.paymentservice.domain.booking.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 예매 생성 시 필요한 티켓 정보 목록을 담는 요청 객체
 */
public record BookingRequest(
        @NotEmpty(message = "티켓 목록은 필수입니다.")
        @Valid
        List<TicketRequest> tickets
) {
}