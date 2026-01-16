package io.why503.paymentservice.global.client.dto;

import lombok.Data;

//추후 테스트 진행
@Data
public class AccountResponse {
    private Long userSq;
    private String name;
    private Integer point;
}
