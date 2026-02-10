package io.why503.performanceservice.global.client.paymentservice.dto;

import java.util.List;

public record TicketCreateRequest(
        List<Long> roundSeatSqs // 티켓 서비스 DTO의 필드명과 일치해야 함!
) {}