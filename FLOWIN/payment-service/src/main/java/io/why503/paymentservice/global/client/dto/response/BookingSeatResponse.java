package io.why503.paymentservice.global.client.dto.response;

import io.why503.paymentservice.domain.ticket.model.enums.DiscountPolicy;

public record BookingSeatResponse(
        Long roundSeatSq,
        DiscountPolicy discountPolicy
) {}