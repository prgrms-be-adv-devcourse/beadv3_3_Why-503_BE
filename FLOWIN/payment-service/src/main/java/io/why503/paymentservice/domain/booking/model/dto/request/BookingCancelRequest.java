// BookingCancelRequest.java
package io.why503.paymentservice.domain.booking.model.dto.request;

import java.util.List;

public record BookingCancelRequest(
        List<Long> ticketSqs, // null이면 전체 취소
        String reason
) {}