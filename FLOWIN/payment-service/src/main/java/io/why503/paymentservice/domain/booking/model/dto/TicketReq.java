package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReq {
    private Long showingSeatSq;
    private Integer originalPrice;
    private Integer finalPrice;
}