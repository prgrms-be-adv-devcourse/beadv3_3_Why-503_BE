package io.why503.reservationservice.domain.booking.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookingEnterRequest (
    @NotNull(message = "회차 ID")
    Long roundSq
){
}
