package io.why503.paymentservice.domain.booking.model.dto.request;

import java.util.List;

/**
 * 예매 취소 요청 시 취소할 티켓 목록과 사유를 담는 객체
 */
public record BookingCancelRequest(
        List<Long> ticketSqs,
        String reason
) {}