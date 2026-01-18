package io.why503.paymentservice.domain.booking.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private Long showingSeatSq;
    private Integer originalPrice;
    private Integer finalPrice;
}