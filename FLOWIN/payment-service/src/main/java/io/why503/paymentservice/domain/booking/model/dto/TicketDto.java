package io.why503.paymentservice.domain.booking.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketDto {
    private Long showingSeatSq;
    private Integer originalPrice;
    private Integer finalPrice;
}