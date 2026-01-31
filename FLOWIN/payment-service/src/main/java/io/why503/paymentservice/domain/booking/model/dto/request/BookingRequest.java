package io.why503.paymentservice.domain.booking.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookingRequest(
        @NotEmpty(message = "티켓 목록은 필수입니다.")
        @Valid
        List<TicketRequest> tickets
) {
}