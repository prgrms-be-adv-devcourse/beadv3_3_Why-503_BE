package io.why503.aiservice.global.client.dto.response;

public record BookingResponse(
        Long sq,
        Long userSq,
        String status,
        String category,
        String genre
) {
}
