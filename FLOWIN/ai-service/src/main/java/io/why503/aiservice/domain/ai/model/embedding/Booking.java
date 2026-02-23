package io.why503.aiservice.domain.ai.model.embedding;

import io.why503.aiservice.global.client.dto.response.BookingResponse;

public record Booking(
        Long sq,
        Long userSq,
        String status,
        String category,
        String genre
) {
    public static Booking from(BookingResponse r) {
        return new Booking(
                r.sq(),
                r.userSq(),
                r.status(),
                r.category(),
                r.genre()
        );
    }
}
