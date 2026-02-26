package io.why503.aiservice.global.client.reservation.model.dto.response;

public record BookingResponse(
        Long sq,
        Long userSq,
        String status,
        String category,
        String genre
) { }