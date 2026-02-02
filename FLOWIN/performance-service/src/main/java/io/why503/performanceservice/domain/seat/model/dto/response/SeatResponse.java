package io.why503.performanceservice.domain.seat.model.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatResponse (
        @NotNull Long seatSq,
        @NotNull Integer seatNo,
        @NotBlank String seatArea,
        @NotNull Integer numInArea
    ){ }
