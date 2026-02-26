package io.why503.paymentservice.global.client.dto.request;

import java.util.List;

public record SeatReserveRequest(
        List<Long> roundSeatSqs
) {
}