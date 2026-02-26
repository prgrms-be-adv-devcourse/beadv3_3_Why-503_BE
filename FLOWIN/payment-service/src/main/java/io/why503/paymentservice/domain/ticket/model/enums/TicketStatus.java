package io.why503.paymentservice.domain.ticket.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 티켓의 판매 단계 및 이용 흐름에 따른 상태 구분
 */
@Getter
@AllArgsConstructor
public enum TicketStatus {
    AVAILABLE("예매가능"),
    RESERVED("선점됨"),
    PAID("결제됨"),
    USED("사용됨"),
    CANCELLED("취소됨");

    private final String description;

}