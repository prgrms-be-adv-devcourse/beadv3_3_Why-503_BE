package io.why503.aiservice.domain.ai.model.embedding;


public record Booking (
        Long sq,
        Long userSq,
        String status,
        String category,
        String genre
) {
}
