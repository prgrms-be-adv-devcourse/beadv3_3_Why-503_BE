package io.why503.paymentservice.global.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//추후 테스트 진행
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String name;
    private Integer point;
}
