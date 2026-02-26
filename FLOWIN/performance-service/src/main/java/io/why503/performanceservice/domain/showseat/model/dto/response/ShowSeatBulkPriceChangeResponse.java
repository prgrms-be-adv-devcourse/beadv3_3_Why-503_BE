package io.why503.performanceservice.domain.showseat.model.dto.response;

public record ShowSeatBulkPriceChangeResponse(
        Long showSq,
        String grade,
        Long price,
        int updatedCount
) {}