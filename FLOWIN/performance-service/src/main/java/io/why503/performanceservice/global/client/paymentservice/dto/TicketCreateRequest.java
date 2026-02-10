package io.why503.performanceservice.global.client.paymentservice.dto;

import java.util.List;

public record TicketCreateRequest(
        List<Long> roundSeatSqs
) {}