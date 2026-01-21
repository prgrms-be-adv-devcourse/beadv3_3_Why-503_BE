package io.why503.performanceservice.domain.round.model.dto;


import java.time.LocalDateTime;

public record RoundRequest(
        Long showSq,
        LocalDateTime roundDt,
        String roundCast,
        RoundStatus roundStatus
) {
}