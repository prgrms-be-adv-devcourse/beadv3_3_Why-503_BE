package io.why503.performanceservice.domain.showseat.model.dto.request;


import jakarta.validation.constraints.NotNull;

public record ShowSeatPriceChangeRequest (
    @NotNull Long price
){ }
