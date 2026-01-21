package io.why503.paymentservice.domain.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPointRequest {
    private Integer point; // 사용할 포인트 금액
}