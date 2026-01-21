package io.why503.performanceservice.domain.round.model.dto;


import lombok.Builder;


import java.time.LocalDateTime;

@Builder
public record RoundResponse(
        Long roundSq,
        Long showSq,
        LocalDateTime roundDt,
        Integer roundNum,
        String roundCast,
        String roundStatusName,
        RoundStatus roundStatus
) {
}