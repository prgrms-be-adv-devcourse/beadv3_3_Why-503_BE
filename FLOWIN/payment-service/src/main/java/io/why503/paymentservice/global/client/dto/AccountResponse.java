package io.why503.paymentservice.global.client.dto;

import lombok.*;

//추후 테스트 진행
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private String name;
    private Integer point;
}
